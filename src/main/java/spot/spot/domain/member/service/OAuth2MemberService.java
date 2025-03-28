package spot.spot.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.dto.response.OAuth2MemberResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberService memberService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2user : {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2MemberResponse oAuth2MemberResponse = new OAuth2MemberResponse(oAuth2User.getAttributes());

        MemberRequest.register userInfo = extractUserInfo(registrationId, oAuth2MemberResponse);

        return registerOrFindUser(userInfo);
    }

    private MemberRequest.register extractUserInfo(String registrationId, OAuth2MemberResponse oAuth2MemberResponse) {
        String nickname = "";
        String email = "";
        String img = "";
        log.info("registrationId : {}", registrationId);

        switch (registrationId) {
            case "kakao":
                nickname = oAuth2MemberResponse.getKakaoNickname();
                email = oAuth2MemberResponse.getKakaoEmail();
                img = oAuth2MemberResponse.getKakaoProfileImage();
                break;
            default:
                throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }

        return MemberRequest.register.builder()
                .email(email)
                .nickname(nickname)
                .img(img)
                .build();
    }

    private OAuth2User registerOrFindUser(MemberRequest.register userInfo) {
        Member existMember = memberService.findByNickname(userInfo.getNickname());

        if (existMember == null) {
            log.info("registerOrFindUser firstLogin");
            memberService.register(userInfo);
            Member newMember = memberService.findByNickname(userInfo.getNickname());
            return new OAuth2Member(newMember);
        }
        log.info("registerOrFindUser notFirstLogin");
        return new OAuth2Member(existMember);
    }
}
