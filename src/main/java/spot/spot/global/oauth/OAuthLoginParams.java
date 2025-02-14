package spot.spot.global.oauth;

import org.springframework.util.MultiValueMap;
import spot.spot.domain.member.OAuthProvider;

public interface OAuthLoginParams {

    //oauth 요청을 위한 파라미터 값
    OAuthProvider oAuthProvider();
    MultiValueMap<String,String> makeBody();
}
