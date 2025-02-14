package spot.spot.global.oauth.kakao;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import spot.spot.domain.member.OAuthProvider;
import spot.spot.global.oauth.OAuthLoginParams;

@Getter
@NoArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams {

    private String authorizationCode;


    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("code",authorizationCode);

        return body;
    }
}
