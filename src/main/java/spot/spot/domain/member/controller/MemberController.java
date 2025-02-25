package spot.spot.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.member._docs.MemberDocs;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.dto.response.TokenDTO;
import spot.spot.domain.member.entity.dto.TokenResponse;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.member.service.TokenService;
import spot.spot.global.security.util.jwt.Token;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController implements MemberDocs {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/modify")
    public void modify(@RequestBody MemberRequest.modify modify, Authentication auth) {
        memberService.modify(modify, auth.getName());
    }

    @GetMapping("/token")
    public TokenResponse getToken(@RequestParam String refreshToken) {
        Token byRefreshToken = tokenService.findByRefreshToken(refreshToken);

        return new TokenResponse(byRefreshToken.getAccessToken());
    }

    @GetMapping("/developer-get-token")
    public TokenDTO getToken4Developer(@RequestParam long id) {
        return memberService.getDeveloperToken(id);
    }
}
