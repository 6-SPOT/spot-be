package spot.spot.domain.job.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.Job2WorkerRequest;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.request.YesOrNo2ClientsRequest;
import spot.spot.domain.job.dto.response.JobDetailResponse;
import spot.spot.domain.job.dto.response.JobSituationResponse;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.entity.Certification;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.mapper.Job4WorkerMapper;
import spot.spot.domain.job.repository.dsl.ChangeJobStatusDsl;
import spot.spot.domain.job.repository.dsl.MatchingDsl;
import spot.spot.domain.job.repository.dsl.SearchingListDsl;
import spot.spot.domain.job.repository.jpa.CertificationRepository;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.job.repository.jpa.MatchingRepository;
import spot.spot.domain.job.service.searching.JobSearchJPQLService;
import spot.spot.domain.job.service.searching.JobSearchNativeQueryService;
import spot.spot.domain.job.service.searching.JobSearchQueryDSLService;
import spot.spot.domain.job.service.searching.JobSearchService;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.repository.AbilityRepository;
import spot.spot.domain.member.repository.WorkerAbilityRepository;
import spot.spot.domain.member.repository.WorkerRepository;
import spot.spot.domain.notification.dto.response.FcmDTO;
import spot.spot.domain.notification.service.FcmUtil;
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
public class Job4WorkerService {

    // 거리 계산용 3가지
    private final JobSearchJPQLService jobSearchJPQLService;
    private final JobSearchNativeQueryService jobSearchNativeService;
    private final JobSearchQueryDSLService jobSearchQueryDSLService;
    // Util
    private final UserAccessUtil userAccessUtil;
    private final FcmUtil fcmUtil;
    private final Job4WorkerMapper job4WorkerMapper;
    // Repo
    private final WorkerRepository workerRepository;
    private final AbilityRepository abilityRepository;
    private final WorkerAbilityRepository workerAbilityRepository;
    private final JobRepository jobRepository;
    private final MatchingRepository matchingRepository;
    private final CertificationRepository certificationRepository;
    private final ChangeJobStatusDsl changeJobStatusDsl;
    private final PayService payService;
    private final JobUtil jobUtil;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final MatchingDsl matchingDsl;
    private final SearchingListDsl searchingListDsl;

    @Transactional
    public void registeringWorker(RegisterWorkerRequest request) {
        Member member = userAccessUtil.getMember();
        Worker worker = job4WorkerMapper.dtoToWorker(request, member);
        workerRepository.save(worker);
        workerAbilityRepository.saveAll(job4WorkerMapper.mapWorkerAbilities(request.strong(), worker, abilityRepository));
    }
    // 구직자 근처 일 리스트 반환
    public Slice<NearByJobResponse> getNearByJobList(String impl, Double lat, Double lng, int zoom, Pageable pageable) {
        Member member = userAccessUtil.getMember();
        lat = lat == null? member.getLat() : lat;
        lng = lng == null? member.getLng() : lng;

        JobSearchService service = switch (impl.toLowerCase()) {
            case "jpql" -> jobSearchJPQLService;
            case "native" -> jobSearchNativeService;
            case "dsl" -> jobSearchQueryDSLService;
            default -> throw new GlobalException(ErrorCode.INVALID_SEARCH_METHOD);
        };
        return service.findNearByJobs(lat, lng, zoom, pageable);
    }
    // 일 하나 상세 확인
    public JobDetailResponse getOneJob (long jobId) {
        Member me = userAccessUtil.getMember();
        return matchingDsl.findOneJobDetail(jobId, me.getId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }
    // 일 신청하기
    public void askingJob2Client(Job2WorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.ATTENDER).build();
        matchingRepository.save(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmUtil.askRequest2ClientMsg(worker.getNickname(), job.getTitle())).build());
    }
    // 일 시작하기
    @Transactional
    public void startJob (Job2WorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.YES);
        PayHistory payHistory = payService.findByJob(job);
        payService.updatePayHistory(payHistory, PayStatus.PROCESS, worker.getNickname());
        changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 시작 알림!").body(
                fcmUtil.getStartedJobMsg(worker.getNickname(), job.getTitle())).build());
    }
    // 의뢰인이 보낸 요청 승낙하기 혹은 거절하기
    @Transactional
    public void yesOrNo2RequestOfClient(YesOrNo2ClientsRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.REQUEST);
        changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes()? MatchingStatus.YES : MatchingStatus.NO);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("요청 승낙 알림!").body(
            fcmUtil.getStartedJobMsg(worker.getNickname(), job.getTitle())).build());
    }

    @Transactional
    public void contiuneJob(Job2WorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Matching matching = matchingRepository.findByMemberAndJob_Id(worker, request.jobId()).orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        jobUtil.withdrawalExistingScheduledTask(matching.getId());
        changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
    }

    @Transactional
    public void certificateJob(Job2WorkerRequest request, MultipartFile file) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Member worker = userAccessUtil.getMember();
        Matching now = matchingRepository
            .findByMemberAndJob_Id(worker, request.jobId())
            .orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        Certification certification = Certification.builder().matching(now).img(url).build();
        certificationRepository.save(certification);
    }

    @Transactional
    public void finishingJob(Job2WorkerRequest request) {
        Member worker = userAccessUtil.getMember();
        Matching matching = matchingRepository
            .findByMemberAndJob_Id(worker, request.jobId())
            .orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START, MatchingStatus.REJECT);
        changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.FINISH);
    }

    public List<JobSituationResponse> getMyJobSituations() {
        Member me = userAccessUtil.getMember();
        return searchingListDsl.findJobSituationsByWorker(me.getId());
    }

    @Transactional
    public void deleteWorker() {
        Member me = userAccessUtil.getMember();
        Worker worker = workerRepository.findById(me.getId()).orElseThrow(() -> new GlobalException(ErrorCode.WORKER_NOT_FOUND));
        workerRepository.delete(worker);
    }
}
