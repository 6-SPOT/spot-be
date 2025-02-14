package spot.spot.global.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spot.spot.global.oauth.kakao.KakaoLoginParams;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OAuthLoginServiceTest {


    @Autowired
    RequestOAuthInfoService requestOAuthInfoService;

    @Test
    void login() {
        //로그인이 되는지 테스트


    }

    @Test
    void accessTokenCheck() {
        //accessToken으로 회원정보 확인
    }
}