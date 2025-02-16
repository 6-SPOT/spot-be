package spot.spot.global.security.util;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class UserAccessUtil {

    private final MemberRepository memberRepository;

    public Optional<Member> getMember() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userName = authentication.getName();
//        return memberRepository.findByName(userName);
        return memberRepository.findById(1L);
    }
}
