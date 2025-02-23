package spot.spot.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.member.entity.dto.MemberRequest;
import spot.spot.domain.member.entity.dto.TokenResponse;
import spot.spot.domain.member.entity.jwt.Token;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.member.service.TokenService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

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

}
