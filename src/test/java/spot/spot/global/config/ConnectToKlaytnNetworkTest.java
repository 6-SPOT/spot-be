package spot.spot.global.config;

import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.exceptions.MessageDecodingException;
import spot.spot.global.klaytn.ConnectToKlaytnNetwork;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.io.IOException;
import java.math.BigInteger;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConnectToKlaytnNetworkTest {

    @Autowired
    ConnectToKlaytnNetwork klaytnNetwork;

    private Caver caver;
    private SingleKeyring keyring;
    private Contract contract;
    private String fromAddress;
    private BigInteger initialBalance;

    /**
     * 애플리케이션 실행시 연결되는 caver, keyring, contract 값을 가져옴
     */
    @BeforeEach
    void connectToContract() {
        caver = klaytnNetwork.getCaver();
        keyring = klaytnNetwork.getSingleKeyring();
        contract = klaytnNetwork.getContract();
        fromAddress = keyring.getAddress();
    }

    @Test
    void checkGetBalance() {
        BigInteger balance = klaytnNetwork.getBalance();
        log.info("contract balance value = {}", balance);
    }

    /**
     * kaia wallet -> smartContract로 kaia 전송
     * @throws InterruptedException
     */
    @Test
    @Order(1)
    void checkDeposit() throws InterruptedException {
        initialBalance = klaytnNetwork.getBalance();
        log.info("initialBalance value = {}", initialBalance);
        String depositTxHash = klaytnNetwork.deposit(1, fromAddress);
        log.info("deposit txHash = {}", depositTxHash);
        waitForTransaction(caver, depositTxHash);
        BigInteger balance = klaytnNetwork.getBalance();
        log.info("balance value = {}", balance);
        Assertions.assertThat(balance).isEqualTo(initialBalance.add(BigInteger.valueOf(1)));
    }

    /**
     * smartContract -> kaia wallet로 kaia전송
     * @throws InterruptedException
     */
    @Test
    @Order(2)
    void checkTransfer() throws InterruptedException {
        initialBalance = klaytnNetwork.getBalance();
        log.info("initialBalance value = {}", initialBalance);
        String transferTxHash = klaytnNetwork.transfer(1, fromAddress);
        log.info("transfer txHash = {}", transferTxHash);
        waitForTransaction(caver, transferTxHash);
        BigInteger balance = klaytnNetwork.getBalance();
        log.info("balance value = {}", balance);
        Assertions.assertThat(balance).isEqualTo(initialBalance.subtract(BigInteger.valueOf(1)));
    }

    /**
     * deposit 시 kaia 변환값이 0일 때 에러 발생
     */
    @Test
    void depositException() {
        Assertions.assertThatThrownBy(() -> klaytnNetwork.deposit(0, fromAddress))
                .isInstanceOf(GlobalException.class);
    }

    /**
     * deposit , transfer 시 잘못된 주소 입력 에러 발생
     * 지갑 주소가 잘못되었기때문에 키링에서 찾을수 없어서 NullPointException 반환
     */
    @Test
    void invalidAddressException() {
        Assertions.assertThatThrownBy(() -> klaytnNetwork.deposit(1, "0xDec5c942E7d596284a5e11C244dE0da3BEFf755c"))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> klaytnNetwork.transfer(1, "0xDec5c942E7d596284a5e11C244dE0da3BEFf755c"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    /**
     * tranfer 시 kaia 변환값이 0일 때 에러 발생
     */
    @Test
    void transferException() {
        Assertions.assertThatThrownBy(() -> klaytnNetwork.transfer(0, fromAddress))
                .isInstanceOf(GlobalException.class);
    }

    /**
     * smartContract transaction이 바로 반영되지 않아 반영될때까지 재시도
     * @param caver
     * @param txHash
     * @throws InterruptedException
     */
    private void waitForTransaction(Caver caver, String txHash) throws InterruptedException {
        int retry = 0;
        while (retry < 10) {
            try {
                TransactionReceipt.TransactionReceiptData receipt = caver.rpc.klay.getTransactionReceipt(txHash).send().getResult();
                if (receipt != null) {
                    return;
                }
            } catch (Exception ignored) {}
            Thread.sleep(3000);
            retry++;
        }
        throw new RuntimeException("Transaction was not mined in time.");
    }
}