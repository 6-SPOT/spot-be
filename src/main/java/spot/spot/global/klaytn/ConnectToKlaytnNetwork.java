package spot.spot.global.klaytn;

import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.transaction.type.SmartContractExecution;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Component
public class ConnectToKlaytnNetwork {
    private static final String PRIVATE_KEY = ContractConstants.PRIVATE_KEY.getValue();
    private static final String CONTRACT_ADDRESS = ContractConstants.CONTRACT_ADDRESS.getValue();
    private static final String NODE_URL = ContractConstants.NODE_URL.getValue();
    private static final String DEPOSIT_METHOD_ID = ContractConstants.DEPOSIT_METHOD_ID.getValue();
    private static final String TRANSFER_METHOD_ID = ContractConstants.TRANSFER_METHOD_ID.getValue();
    private static final String ABI_JSON = ContractConstants.ABI_JSON.getValue();
    private Caver caver;
    private SingleKeyring singleKeyring;
    private Contract contract;

    @PostConstruct
    public CaverConnection connect() {
        caver = new Caver(NODE_URL);
        singleKeyring = connectWallet();
        contract = connectSmartContract();
        return new CaverConnection(caver, singleKeyring, contract);
    }

    private SingleKeyring connectWallet() {
        SingleKeyring keyring = caver.wallet.keyring.createFromPrivateKey(PRIVATE_KEY);
        caver.wallet.add(keyring);
        return keyring;
    }

    private Contract connectSmartContract(){
        try {
            Contract contract = caver.contract.create(ABI_JSON, CONTRACT_ADDRESS);
            return contract;
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FAIL_CREATE_CONTRACT);
        }
    }

    public BigInteger getBalance() {
        BigInteger balance = null;
        try {
            List<Type> result = contract.getMethod("getBalance").call(Collections.emptyList());
            balance = (BigInteger) result.get(0).getValue();
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.FAIL_CONNECT_KLAYTN_NETWORK);
        }
        return balance;
    }

    public String deposit(int amount, String fromAddress){
        if(amount <= 0) {
            throw new GlobalException(ErrorCode.LOW_AMOUNT);
        }
        BigInteger nonce = getNonce(fromAddress);
        BigInteger gasPrice = getGasPrice();
        SmartContractExecution tx = new SmartContractExecution.Builder()
                .setFrom(fromAddress)
                .setTo(CONTRACT_ADDRESS)
                .setGas(BigInteger.valueOf(500000))
                .setGasPrice(gasPrice)
                .setNonce(nonce)
                .setValue(String.valueOf(amount))
                .setInput(DEPOSIT_METHOD_ID)
                .build();

        return getTxHash(fromAddress, tx);
    }

    public String transfer(int amount, String toAddress) {
        if(amount <= 0) {
            throw new GlobalException(ErrorCode.LOW_AMOUNT);
        }
        BigInteger amountBigInt = BigInteger.valueOf(amount);
        BigInteger nonce = getNonce(toAddress);
        BigInteger gasPrice = getGasPrice();
        String valueHex = Numeric.toHexStringWithPrefixZeroPadded(amountBigInt, 64);
        String inputData = TRANSFER_METHOD_ID + valueHex.substring(2);

        SmartContractExecution tx = new SmartContractExecution.Builder()
                .setFrom(toAddress)
                .setTo(CONTRACT_ADDRESS)
                .setGas(BigInteger.valueOf(500000))
                .setGasPrice(gasPrice)
                .setNonce(nonce)
                .setInput(inputData)
                .build();
        return getTxHash(toAddress, tx);
    }

    private BigInteger getNonce(String toAddress) {
        BigInteger nonce = null;
        try {
            nonce = caver.rpc.klay.getTransactionCount(toAddress, DefaultBlockParameterName.LATEST).send().getValue();
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.NOT_ALLOW_FROM_ADDRESS);
        }
        return nonce;
    }

    private String getTxHash(String fromAddress, SmartContractExecution tx) {
        tx.setKlaytnCall(caver.rpc.klay);
        try {
            tx.fillTransaction();
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FAIL_CONNECT_KLAYTN_NETWORK);
        }

        try {
            caver.wallet.sign(fromAddress, tx);
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FAIL_CONNECT_KLAYTN_NETWORK);
        }

        try {
            Bytes32 response = caver.rpc.klay.sendRawTransaction(tx.getRLPEncoding()).send();
            return response.getResult();
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FAIL_CONNECT_KLAYTN_NETWORK);
        }
    }

    private BigInteger getGasPrice() {
        BigInteger gasPrice = null;
        try {
            gasPrice = caver.rpc.klay.getGasPrice().send().getValue();
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FAIL_CONNECT_KLAYTN_NETWORK);
        }
        return gasPrice;
    }

    public Caver getCaver() {
        return caver;
    }

    public SingleKeyring getSingleKeyring() {
        return singleKeyring;
    }

    public Contract getContract() {
        return contract;
    }
}

