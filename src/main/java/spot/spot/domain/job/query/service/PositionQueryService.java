package spot.spot.domain.job.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.request.PositionRequest;
import spot.spot.domain.job.query.dto.response.PositionResponse;
import spot.spot.domain.job.query.mapper.PositionQueryMapper;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionQueryService {

    private final SimpMessageSendingOperations template;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PositionQueryMapper positionQueryMapper;

    public void sendPosition (PositionRequest request, String atk) {
        String token = jwtUtil.separateBearer(atk);
        Member sender = memberRepository.findById(
            Long.valueOf(jwtUtil.getUserInfoFromToken(token).getSubject())
        ).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        PositionResponse response = positionQueryMapper.toPositionResponse(sender, request);
        log.info("전송 위치: /topic/job/{}", request.jobId());
        log.info("전송 내용: 위도={}, 경도={}", request.lat(), request.lng());
        template.convertAndSend("/topic/job/" + request.jobId(), response);
    }
}
