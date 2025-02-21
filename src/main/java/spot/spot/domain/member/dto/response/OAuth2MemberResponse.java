package spot.spot.domain.member.dto.response;

import java.util.Map;

public class OAuth2MemberResponse {

    private final Map<String, Object> properties;
    private final Map<String, Object> kakaoAccount;

    public OAuth2MemberResponse(Map<String, Object> attribute) {
        this.properties = (Map<String, Object>) attribute.get("properties");
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
    }

    public String getKakaoNickname() {
        return properties.get("nickname").toString();
    }

    public String getKakaoProfileImage() {
        return properties.get("profile_image").toString();
    }

    public String getKakaoEmail(){ return kakaoAccount.get("email").toString();}
}
