package spot.spot.global.security.util;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JasyptUtil {
    private final StringEncryptor stringEncryptor;

    @Autowired
    public JasyptUtil(@Qualifier("jasyptEncryptor") StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }
    /*
     *  문자열을 Jasypt로 암호화 하는 매서드
     *  @Param plain Text (String) 암호화할 문자열
     *  @return ENC(암호화된 문자열)
     * */
    public String encrypt(String str) {
        return "ENC (" + stringEncryptor.encrypt(str)+ ")";
    }
}
