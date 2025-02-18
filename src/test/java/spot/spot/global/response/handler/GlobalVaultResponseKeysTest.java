package spot.spot.global.response.handler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Slf4j
class GlobalVaultResponseKeysTest {

    @Autowired
    GlobalVaultResponseKeys handler;

    /**
     * vault서버 키를 확인할수 있는 테스트입니다.
     */
//    @Test
//    void checkResponseJson() {
//        Map<String, String> vaultKeys = handler.getVaultKeys();
//
//        for (String vk : vaultKeys.keySet()) {
//            log.info("key= {} : value ={}", vk, vaultKeys.get(vk));
//        }
//    }
}