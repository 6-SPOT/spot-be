package spot.spot.domain.job.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.response.JobCertifiationResponse;
import spot.spot.domain.job.command.mapper.WorkerCommandMapper;
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
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.repository.AbilityRepository;
import spot.spot.domain.member.repository.WorkerAbilityRepository;
import spot.spot.domain.member.repository.WorkerRepository;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
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
    // Util
    private final UserAccessUtil userAccessUtil;
    private final FcmAsyncSendingUtil fcmAsyncSendingUtil;
    private final WorkerCommandMapper workerCommandMapper;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final ReservationCancelUtil reservationCancelUtil;
    // Repo
    private final WorkerRepository workerRepository;
    private final AbilityRepository abilityRepository;
    private final WorkerAbilityRepository workerAbilityRepository;
    private final MatchingRepository matchingRepository;
    private final CertificationRepository certificationRepository;
    private final ChangeJobStatusCommandDsl changeJobStatusCommandDsl;
    private final PayService payService;
    private final MemberService memberService;
    private final FcmMessageUtil fcmMessageUtil;

    @Transactional
    public void registeringWorker(RegisterWorkerRequest request) {
        Member member = userAccessUtil.getMember();
        Worker worker = workerCommandMapper.dtoToWorker(request, member);
        workerRepository.save(worker);
        workerAbilityRepository.saveAll(
            workerCommandMapper.mapWorkerAbilities(request.strong(), worker, abilityRepository));
    }
    public void askingJob2Client(ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.ATTENDER).build();
        matchingRepository.save(matching);
        fcmAsyncSendingUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmMessageUtil.askRequest2ClientMsg(worker.getNickname(), job.getTitle())).build());
    }
    @Transactional
    public void startJob (ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.YES);
        payService.updateStartJob(job, worker);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
        fcmAsyncSendingUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 시작 알림!").body(
            fcmMessageUtil.getStartedJobMsg(worker.getNickname(), job.getTitle())).build());
    }
    @Transactional
    public void yesOrNo2RequestOfClient(YesOrNoClientsRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.REQUEST);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes()? MatchingStatus.YES : MatchingStatus.NO);
        fcmAsyncSendingUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("요청 승낙 알림!").body(
            fcmMessageUtil.getStartedJobMsg(worker.getNickname(), job.getTitle())).build());
    }
    @Transactional
    public void contiuneJob(ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Matching matching = matchingRepository.findByMemberAndJob_Id(worker, request.jobId()).orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        reservationCancelUtil.withdrawalExistingScheduledTask(matching.getId());
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
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
        return new JobCertifiationResponse(url);
    }
    @Transactional
    public void finishingJob(ChangeStatusWorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Matching matching = matchingRepository
            .findByMemberAndJob_Id(worker, request.jobId())
            .orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START, MatchingStatus.REJECT);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.FINISH);
    }
    @Transactional
    public void deleteWorker() {
        Member me = userAccessUtil.getMember();
        Worker worker = workerRepository.findById(me.getId()).orElseThrow(() -> new GlobalException(ErrorCode.WORKER_NOT_FOUND));
        workerRepository.delete(worker);
    }
}
