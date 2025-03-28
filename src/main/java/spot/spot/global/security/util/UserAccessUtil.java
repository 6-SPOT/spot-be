package spot.spot.global.security.util;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Component
@RequiredArgsConstructor
public class UserAccessUtil {

    private final MemberRepository memberRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true) // 현 조회 중인 유저의 영속성 유지
    public Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        entityManager.merge(member);
        return member;
    }
}
