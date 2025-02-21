package spot.spot.global.security.util;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class UserAccessUtil {

    private final MemberRepository memberRepository;

    public Optional<Member> getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findById(Long.parseLong(authentication.getName()));
    }
}
