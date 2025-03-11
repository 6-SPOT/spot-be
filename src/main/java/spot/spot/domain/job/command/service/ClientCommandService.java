package spot.spot.domain.job.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.mapper.ClientCommandMapper;
import spot.spot.domain.job.command.service._docs.ClientCommandServiceDocs;
import spot.spot.domain.job.command.util.ReservationCancelUtil;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;
import spot.spot.domain.job.command.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.repository.dsl.ChangeJobStatusCommandDsl;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.notification.dto.response.FcmDTO;
import spot.spot.domain.notification.service.FcmUtil;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCommandService implements ClientCommandServiceDocs {
    // Util
    private final DistanceCalculateUtil distanceCalculateUtil;
    private final UserAccessUtil userAccessUtil;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    // Mapper
    private final ClientCommandMapper clientCommandMapper;
    private final JobRepository jobRepository;
    // JPA
    private final MatchingRepository matchingRepository;
    // Query dsl
    private final ChangeJobStatusCommandDsl changeJobStatusCommandDsl;
    private final FcmUtil fcmUtil;
    private final PayService payService;
    private final MemberRepository memberRepository;
    private final ReservationCancelUtil reservationCancelUtil;

    public RegisterJobResponse registerJob(RegisterJobRequest request, MultipartFile file) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Member client = userAccessUtil.getMember();
        Job newJob = jobRepository.save(clientCommandMapper.registerRequestToJob(url, request, " "));

        Matching matching = Matching.builder()
            .member(client)
            .job(newJob)
            .status(MatchingStatus.OWNER)
            .build();
        matchingRepository.save(matching);
        return RegisterJobResponse.create(newJob.getId());
    }

    public void askingJob2Worker (ChangeStatusClientRequest request) {
        Member worker = memberRepository
            .findById(request.workerId()).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(request.workerId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.REQUEST).build();
        matchingRepository.save(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmUtil.askRequest2WorkerMsg(worker.getNickname(), job.getTitle())).build());
    }

    @Transactional
    public void yesOrNo2RequestOfWorker(YesOrNoWorkersRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository
            .findById(request.attenderId()).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.ATTENDER);
        changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes()? MatchingStatus.YES : MatchingStatus.NO);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 시작 알림!").body(
            fcmUtil.requestAcceptedBody(owner.getNickname(), worker.getNickname(), job.getTitle())).build());
    }

    @Transactional
    public void requestWithdrawal(ChangeStatusClientRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository
            .findById(request.workerId()).orElseThrow(() -> new GlobalException(
                ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START);
        Matching matching = changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.SLEEP);
        reservationCancelUtil.scheduledSleepMatching2Cancel(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("혹시 잠수 타셨나요??").body(
            fcmUtil.requestAcceptedBody(owner.getNickname(), worker.getNickname(), job.getTitle())).build());
    }

    @Transactional
    public Job updateTidToJob(Job findJob, String tid) {
        findJob.setTid(tid);
        return findJob;
    }

    @Transactional
    public void confirmOrRejectJob(YesOrNoWorkersRequest request) {
        Member worker = memberRepository.findById(request.attenderId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        changeJobStatusCommandDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.FINISH);
        Matching matching = changeJobStatusCommandDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes() ? MatchingStatus.CONFIRM : MatchingStatus.REJECT);
        int payAmountByMatchingJob = payService.findPayAmountByMatchingJob(matching.getId());
        if (matching.getStatus().equals(MatchingStatus.CONFIRM)) {
            payService.payTransfer(String.valueOf(worker.getId()), payAmountByMatchingJob, matching.getJob());
        }
    }
}
