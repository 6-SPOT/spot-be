package spot.spot.global.oauth.naver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import spot.spot.domain.member.OAuthProvider;
import spot.spot.global.oauth.OAuthLoginParams;

@Getter
@NoArgsConstructor
public class NaverLoginParams implements OAuthLoginParams {
    //네이버 api요청을 위한 값
    private String authorizationCode;
    private String state = "spotTemporary250211";   //임시로 설정


    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("code",authorizationCode);
        body.add("state",state);
        return body;
    }
}
