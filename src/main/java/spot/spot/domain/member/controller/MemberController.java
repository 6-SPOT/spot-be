package spot.spot.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.member._docs.MemberDocs;
import spot.spot.domain.member.dto.response.TokenDTO;
import spot.spot.domain.member.service.MemberService;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController implements MemberDocs {

    private final MemberService memberService;

    @GetMapping("/developer-get-token")
    public TokenDTO getToken4Developer(@RequestParam long id) {
        return memberService.getDeveloperToken(id);
    }
}
