package spot.spot.domain.member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.oauth.AuthTokensGenerator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<Member>> findAll() {
        return ResponseEntity.ok(memberRepository.findAll());
    }

    @GetMapping("/{accessToken}")
    public ResponseEntity<Member> findByAccessToken(@PathVariable String accessToken) {
        Long memberId = authTokensGenerator.extractMemberId(accessToken);
        return ResponseEntity.ok(memberRepository.getById(memberId));
    }

    @PostMapping("/{accessToken}")
    public ResponseEntity<Member> saveAdditionalInfo(@PathVariable String accessToken,@RequestParam String phone,@RequestParam double lat,@RequestParam double lng){
        Long memberId = authTokensGenerator.extractMemberId(accessToken);
        memberService.updateMember(memberId,phone,lat,lng);

        return ResponseEntity.ok(memberRepository.getById(memberId));
    }
}