package spot.spot.global.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokens {
    //    사용자에게 내려주는 서비스의 인증 토큰 값입니다.
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiredsIn;

    public static AuthTokens of(String accessToken,String refreshToken,String grantType,Long expiredsIn){
        return new AuthTokens(accessToken,refreshToken,grantType,expiredsIn);
    }
}
