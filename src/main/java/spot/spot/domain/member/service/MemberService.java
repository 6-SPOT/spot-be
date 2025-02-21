package spot.spot.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.dto.response.TokenDTO;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.entity.MemberRole;
import spot.spot.domain.member.repository.MemberRepository;

import java.util.Optional;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

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

    public TokenDTO getDeveloperToken(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        return TokenDTO.builder().accessToken(jwtUtil.createDeveloperToken(member)).build();
    }


    @Transactional
    public Member findByNickname(String nickname) {
        Optional<Member> findMember = memberRepository.findByNickname(nickname);
        return findMember.orElse(null);
    }

    @Transactional
    public Member findById(Long id) {
        Optional<Member> findMember = memberRepository.findById(id);
        return findMember.orElse(null);
    }
}
