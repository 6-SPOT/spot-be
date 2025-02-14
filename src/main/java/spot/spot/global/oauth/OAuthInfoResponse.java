package spot.spot.global.oauth;

import spot.spot.domain.member.OAuthProvider;

public interface OAuthInfoResponse {
    //Access Token 으로 요청한 외부 API 프로필 응답값을
    //우리 서비스의 Model 로 변환시키기 위한 인터페이스입니다.
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
}