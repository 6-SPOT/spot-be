package spot.spot.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.dto.MemberRequest;
import spot.spot.domain.member.entity.dto.MemberRole;
import spot.spot.domain.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void register(MemberRequest.register register) {

        Member member = Member.builder()
                .nickname(register.getNickname())
                .email(register.getEmail())
                .point(0)
                .memberRole(MemberRole.MEMBER)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public Member findByNickname(String nickname) {
        Optional<Member> findMember = memberRepository.findByNickname(nickname);
        if(findMember.isEmpty()) return null;

        return findMember.get();
    }

    @Transactional
    public Member findById(Long id) {
        Optional<Member> findMember = memberRepository.findById(id);
        if(findMember.isEmpty()) return null;

        return findMember.get();
    }
}
