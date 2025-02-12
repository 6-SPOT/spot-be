package spot.spot.global.security.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import spot.spot.global.security.util.JasyptUtil;

@SpringBootTest

public class IssueEncryptString {

    @Value("${jasypt.encryptor.password}")
    private String key;

    private JasyptUtil jasyptUtil;

    @BeforeEach
    void setUp() {
        // 암호화를 담당하는 암호화 객체 설정
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(key);
        config.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setConfig(config);

        jasyptUtil = new JasyptUtil(encryptor);
    }

    @Test
    @DisplayName("1. YAML에서 가져온 키로 암호화/복호화 테스트")
    void testEncryptionDecryption() {
        String str = "MySecretData";
        // 암호화 및 복호화 수행
        String encryptedText = jasyptUtil.encrypt(str);
        System.out.println(encryptedText);
    }

}
