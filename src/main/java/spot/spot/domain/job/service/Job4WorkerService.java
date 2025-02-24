package spot.spot.domain.job.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.dto.request.Job4ClientRequest;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.mapper.Job4WorkerMapper;
import spot.spot.domain.job.repository.dsl.ChangeJobStatusDsl;
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
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

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
    private final ChangeJobStatusDsl changeJobStatusDsl;

    // 일반 회원 -> 구직자
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
    public NearByJobResponse getOneJob (long jobId) {
        return job4WorkerMapper.toNearByJobResponse(
                jobRepository.findById(jobId).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND)));
    }
    // 일 신청하기
    public void askingJob2Client(Job4ClientRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.ATTENDER).build();
        matchingRepository.save(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmUtil.askRequest2ClientMsg(worker.getNickname(), job.getTitle())).build());
    }
    // 일 시작하기
    public void startJob (Job4ClientRequest request) {
        Member worker = userAccessUtil.getMember();
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.YES);
        changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.START);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 시작 알림!").body(
            fcmUtil.getStartedJobMsg(worker.getNickname(), job.getTitle())).build());
    }
}
