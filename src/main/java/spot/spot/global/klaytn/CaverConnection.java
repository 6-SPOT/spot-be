package spot.spot.global.klaytn;

import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CaverConnection {

    private Caver caver;
    private SingleKeyring singleKeyring;
    private Contract contract;

}
