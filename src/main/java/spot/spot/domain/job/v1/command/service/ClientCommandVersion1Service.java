package spot.spot.domain.job.v1.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.repository.dsl.ChangeJobStatusCommandDsl;
import spot.spot.domain.job.command.util.ReservationCancelUtil;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
import spot.spot.domain.notification.command.service.FcmAsyncSendingUtil;
import spot.spot.domain.notification.command.service.FcmMessageUtil;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCommandVersion1Service {

    private final UserAccessUtil userAccessUtil;
    private final MemberRepository memberRepository;
    private final ChangeJobStatusCommandDsl changeJobStatusCommandDsl;
    private final ReservationCancelUtil reservationCancelUtil;
    private final FcmAsyncSendingUtil fcmAsyncSendingUtil;
    private final FcmMessageUtil fcmMessageUtil;

    @Transactional
    public void requestWithdrawalTest(ChangeStatusClientRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository
            .findById(request.workerId()).orElseThrow(() -> new GlobalException(
                ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START);
        Matching matching = changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.SLEEP);
        reservationCancelUtil.scheduledSleepMatching2CancelTest(matching);
        fcmAsyncSendingUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("혹시 잠수 타셨나요??").body(
            fcmMessageUtil.requestAcceptedBody(owner.getNickname(), worker.getNickname(), job.getTitle())).build());
    }
}
