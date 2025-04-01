package spot.spot.domain.job.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.dto.request.ConfirmOrRejectRequest;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.mapper.ClientCommandMapper;
import spot.spot.domain.job.command.mapper.NotificationMapper;
import spot.spot.domain.job.command.service._docs.ClientCommandServiceDocs;
import spot.spot.domain.job.command.util.ReservationCancelUtil;
import spot.spot.domain.job.command.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.repository.dsl.ChangeJobStatusCommandDsl;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberQueryRepository;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
import spot.spot.domain.notification.command.entity.NoticeType;
import spot.spot.domain.notification.command.repository.NotificationRepository;
import spot.spot.domain.notification.command.service.FcmAsyncSendingUtil;
import spot.spot.domain.notification.command.service.FcmMessageUtil;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCommandService implements ClientCommandServiceDocs {
    // Config
    private final GeometryFactory geometryFactory;
    // Util
    private final UserAccessUtil userAccessUtil;
    private final FcmAsyncSendingUtil fcmAsyncSendingUtil;
    private final ReservationCancelUtil reservationCancelUtil;
    private final FcmMessageUtil fcmMessageUtil;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final PayService payService;
    private final RetryTemplate retryTemplate;
    // Mapper
    private final ClientCommandMapper clientCommandMapper;
    private final NotificationMapper notificationMapper;
    // JPA
    private final MatchingRepository matchingRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final JobRepository jobRepository;
    // Query dsl
    private final ChangeJobStatusCommandDsl changeJobStatusCommandDsl;

    @Transactional
    public RegisterJobResponse registerJob(RegisterJobRequest request, MultipartFile file) {
        String url = null;
        if(file != null) url = awsS3ObjectStorage.uploadFile(file);
        Member client = userAccessUtil.getMember();
        Job job = jobRepository.save(clientCommandMapper.registerRequestToJob(url, request, geometryFactory));
        Matching matching = clientCommandMapper.toMatching(client, job, MatchingStatus.OWNER);
        matchingRepository.save(matching);
        return clientCommandMapper.toRegisterJobResponse(job.getId());
    }

    public void askingJob2Worker (ChangeStatusClientRequest request) {
        Member worker = memberRepository.findById(request.workerId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(request.workerId(), request.jobId());
        Matching matching = clientCommandMapper.toMatching(worker, job, MatchingStatus.REQUEST);
        matchingRepository.save(matching);
        FcmDTO msg = fcmMessageUtil.askingJob2WorkerMsg(userAccessUtil.getMember().getNickname(), worker.getNickname(), job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("해결사에게 일 의뢰: FCM 전송 시도 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(worker.getId(), msg);
            return null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg, NoticeType.JOB, userAccessUtil.getMember(), worker.getId()));
    }

    @Transactional
    public void yesOrNo2RequestOfWorker(YesOrNoWorkersRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository.findById(request.attenderId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.ATTENDER);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes()? MatchingStatus.YES : MatchingStatus.NO);
        FcmDTO msg = request.isYes()? fcmMessageUtil.sayYes2WorkerMsg(owner.getNickname(),
            worker.getNickname(), job.getTitle()) : fcmMessageUtil.sayNo2WorkerMsg(owner.getNickname(),
            worker.getNickname(), job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("의뢰인의 일 해결 요청: FCM 전송 시도 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(worker.getId(), msg);
            return null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg, NoticeType.JOB, userAccessUtil.getMember(), worker.getId()));
    }

    @Transactional
    public void requestWithdrawal(ChangeStatusClientRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository.findById(request.workerId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START);
        Matching matching = changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.SLEEP);
        reservationCancelUtil.scheduledSleepMatching2Cancel(matching);
        FcmDTO msg = fcmMessageUtil.doYouSleepMsg(owner.getNickname(), worker.getNickname(), job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("해결사 취소 요청 (NO SHOW): FCM 전송 시도 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(worker.getId(), msg);
            return null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg,NoticeType.JOB,owner, worker.getId()));
    }

    @Transactional
    public void confirmOrRejectJob(ConfirmOrRejectRequest request) {
        Member worker = memberQueryRepository.findOneFinisherOfJob(request.jobId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.FINISH);
        Matching matching = changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes() ? MatchingStatus.CONFIRM : MatchingStatus.REJECT);

        if (matching.getStatus().equals(MatchingStatus.CONFIRM)) {
//            payService.payTransfer(String.valueOf(worker.getId()),
//                payService.findPayAmountByMatchingJob(matching.getId(), worker.getId()),
//                matching.getJob());
            FcmDTO msg = fcmMessageUtil.confirm2WorkerMsg(userAccessUtil.getMember().getNickname(),
                worker.getNickname(), job.getTitle());
            retryTemplate.execute(context -> {
                log.warn("해결사 확정 재시도 : FCM 전송 시도 [재시도 횟수 {}]", context.getRetryCount());
                fcmAsyncSendingUtil.singleFcmSend(worker.getId(), msg);
                return null;
            });
        }else {
            FcmDTO msg = fcmMessageUtil.reject2WorkerMsg(userAccessUtil.getMember().getNickname(),
                worker.getNickname(), job.getTitle());
            retryTemplate.execute(context -> {
                log.warn("해결사 거절 재시도: FCM 전송 시도 [재시도 횟수 {}]", context.getRetryCount());
                fcmAsyncSendingUtil.singleFcmSend(worker.getId(), msg);
                return null;
            });
        }
    }

    @Transactional
    public Job updateTidToJob(Job findJob, String tid) {
        findJob.setTid(tid);
        return findJob;
    }
}
