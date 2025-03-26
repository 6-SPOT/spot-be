package spot.spot.domain.job.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.response.JobCertifiationResponse;
import spot.spot.domain.job.command.mapper.NotificationMapper;
import spot.spot.domain.job.command.mapper.WorkerCommandMapper;
import spot.spot.domain.job.command.repository.dsl.WorkerUpdatingCommandDsl;
import spot.spot.domain.job.command.service._docs.WorkerCommandServiceDocs;
import spot.spot.domain.job.command.util.ReservationCancelUtil;
import spot.spot.domain.job.command.dto.request.ChangeStatusWorkerRequest;
import spot.spot.domain.job.command.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoClientsRequest;
import spot.spot.domain.job.command.entity.Certification;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.repository.dsl.ChangeJobStatusCommandDsl;
import spot.spot.domain.job.command.repository.jpa.CertificationRepository;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.repository.AbilityRepository;
import spot.spot.domain.member.repository.WorkerAbilityRepository;
import spot.spot.domain.member.repository.WorkerRepository;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
import spot.spot.domain.notification.command.entity.NoticeType;
import spot.spot.domain.notification.command.repository.NotificationRepository;
import spot.spot.domain.notification.command.service.FcmAsyncSendingUtil;
import spot.spot.domain.notification.command.service.FcmMessageUtil;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerCommandService implements WorkerCommandServiceDocs {
    // Config
    private final GeometryFactory geometryFactory;
    // Util
    private final UserAccessUtil userAccessUtil;
    private final FcmAsyncSendingUtil fcmAsyncSendingUtil;
    private final WorkerCommandMapper workerCommandMapper;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final ReservationCancelUtil reservationCancelUtil;
    private final RetryTemplate retryTemplate;
    // Repo
    private final WorkerRepository workerRepository;
    private final AbilityRepository abilityRepository;
    private final WorkerAbilityRepository workerAbilityRepository;
    private final MatchingRepository matchingRepository;
    private final CertificationRepository certificationRepository;
    private final ChangeJobStatusCommandDsl changeJobStatusCommandDsl;
    private final WorkerUpdatingCommandDsl workerUpdatingCommandDsl;
    private final PayService payService;
    private final FcmMessageUtil fcmMessageUtil;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final JobRepository jobRepository;

    @Transactional
    public void registeringWorker(RegisterWorkerRequest request) {
        Member member = userAccessUtil.getMember();
        Point location = workerCommandMapper.mapLatLngToPoint(request.lat(), request.lng(), geometryFactory);
        Worker worker = workerCommandMapper.dtoToWorker(request, member);
        workerUpdatingCommandDsl.updateLocationById(member.getId(), request.lat(), request.lng(), location);
        workerRepository.save(worker);
        workerAbilityRepository.saveAll(workerCommandMapper.mapWorkerAbilities(request.strong(), worker, abilityRepository));
    }

    public void askingJob2Client(ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.ATTENDER).build();
        matchingRepository.save(matching);
        Member owner = changeJobStatusCommandDsl.getJobsOwner(job.getId());
        FcmDTO msg = fcmMessageUtil.askingJob2ClientMsg(owner.getNickname(), worker.getNickname(),job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("의뢰인의 일 해결 요청: FCM 전송 시도 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(owner.getId(), msg);
            return  null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg, NoticeType.JOB, worker, owner.getId()));
    }

    @Transactional
    public void startJob (ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.YES);
        payService.updateStartJob(job, worker);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
        Member owner = changeJobStatusCommandDsl.getJobsOwner(job.getId());
        FcmDTO msg = fcmMessageUtil.startJob2ClientMsg(owner.getNickname(), worker.getNickname(), job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("해결사의 일 시작 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(owner.getId(), msg);
            return  null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg,NoticeType.JOB, worker,
            owner.getId()));
    }

    @Transactional
    public void yesOrNo2RequestOfClient(YesOrNoClientsRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.REQUEST);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes()? MatchingStatus.YES : MatchingStatus.NO);
        Member owner = changeJobStatusCommandDsl.getJobsOwner(job.getId());
        FcmDTO msg = request.isYes()?
            fcmMessageUtil.sayYes2ClientMsg(owner.getNickname(), worker.getNickname(), job.getTitle()) :
            fcmMessageUtil.sayNo2ClientMsg(owner.getNickname(), worker.getNickname(), job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("해결사의 일 의뢰 승낙 혹은 거절 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(owner.getId(), msg);
            return  null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg,NoticeType.JOB, worker,
            owner.getId()));
    }

    @Transactional
    public void continueJob(ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Matching matching = matchingRepository.findByMemberAndJob_Id(worker, request.jobId()).orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        reservationCancelUtil.withdrawalExistingScheduledTask(matching.getId());
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
        Member owner = changeJobStatusCommandDsl.getJobsOwner(matching.getId());
        FcmDTO msg = fcmMessageUtil.continueJobMsg(owner.getNickname(), worker.getNickname());
        retryTemplate.execute(context -> {
            log.warn("해결사의 일 재개 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(owner.getId(), msg);
            return  null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg,NoticeType.JOB, worker,
            owner.getId()));
    }

    @Transactional
    public JobCertifiationResponse certificateJob(ChangeStatusWorkerRequest request, MultipartFile file) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Member worker = userAccessUtil.getMember();
        Matching now = matchingRepository
            .findByMemberAndJob_Id(worker, request.jobId())
            .orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        Certification certification = Certification.builder().matching(now).img(url).build();
        certificationRepository.save(certification);
        return workerCommandMapper.toJobCertificationResponse(url);
    }

    @Transactional
    public void finishingJob(ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Matching matching = matchingRepository
            .findByMemberAndJob_Id(worker, request.jobId())
            .orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START, MatchingStatus.REJECT);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.FINISH);
        Job job = jobRepository.findById(matching.getJob().getId()).orElseThrow(()-> new GlobalException(ErrorCode.JOB_NOT_FOUND));

        Member owner = changeJobStatusCommandDsl.getJobsOwner(job.getId());
        FcmDTO msg = fcmMessageUtil.startJob2ClientMsg(owner.getNickname(), worker.getNickname(), job.getTitle());
        retryTemplate.execute(context -> {
            log.warn("해결사의 일 성공 알림 [재시도 횟수 {}]", context.getRetryCount());
            fcmAsyncSendingUtil.singleFcmSend(owner.getId(), msg);
            return  null;
        });
        notificationRepository.save(notificationMapper.toNotification(msg,NoticeType.JOB, worker, owner.getId()));
    }

    @Transactional
    public void deleteWorker() {
        Member me = userAccessUtil.getMember();
        Worker worker = workerRepository.findById(me.getId()).orElseThrow(() -> new GlobalException(ErrorCode.WORKER_NOT_FOUND));
        workerRepository.delete(worker);
    }
}
