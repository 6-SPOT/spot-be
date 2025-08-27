package spot.spot.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.dto.response.LoginResponseDto;
import spot.spot.domain.member.dto.response.TokenDTO;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.MemberRole;
import spot.spot.domain.member.repository.MemberQueryRepository;
import spot.spot.domain.member.repository.MemberRepository;

import java.util.Optional;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final LoginFakeApiService loginFakeApiService;
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

    public TokenDTO getDeveloperTokenWithFakeApi(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new GlobalException(
                ErrorCode.MEMBER_NOT_FOUND));
        LoginResponseDto loginResponseDto = loginFakeApiService.loginfakeAPIRequest(String.valueOf(id), LoginResponseDto.class);

        return TokenDTO.builder().accessToken(loginResponseDto.data().accessToken()).build();
    }

    @Transactional
    public Member findByNickname(String nickname) throws GlobalException {
        Optional<Member> findMember = memberRepository.findByNickname(nickname);
        if (findMember.isEmpty()) {
            return null;
        }
        return findMember.get();
    }

    @Transactional
    public Member findById(String memberId) {
        if(memberId == null || memberId.isEmpty()) throw new GlobalException(ErrorCode.EMPTY_MEMBER);
        long id = Long.parseLong(memberId);
        return memberRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public void modify(MemberRequest.modify modify, String memberId) {
        Long parseMemberId = Long.parseLong(memberId);
        memberQueryRepository.updateMember(parseMemberId, modify);
    }

    public Member findMemberByIdOrNickname(String id, String nickname) {
        log.info("findMemberByIdOrNickname called with id = {}, nickname = {}", id, nickname);
        if (id != null) {
            return findById(id);
        } else if (nickname != null) {
            return findByNickname(nickname);
        }
        throw new GlobalException(ErrorCode.EMPTY_MEMBER);
    }

    public Member findMemberByJobInfo(Job job) {
        return memberQueryRepository.findMemberByMatchingOwner(job)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
