package spot.spot.domain.job.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.dto.request.Client2JobRequest;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.response.JobWithOwnerAndErrorCodeResponse;
import spot.spot.domain.job.dto.response.JobWithOwnerReponse;
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
import spot.spot.domain.member.repository.MemberRepository;
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

    @Transactional
    public void registeringWorker(RegisterWorkerRequest request) {
        Member member = userAccessUtil.getMember();
        Worker worker = job4WorkerMapper.dtoToWorker(request, member);
        workerRepository.save(worker);
        workerAbilityRepository.saveAll(job4WorkerMapper.mapWorkerAbilities(request.strong(), worker, abilityRepository));
    }

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

    public NearByJobResponse getOneJob (long jobId) {
        return job4WorkerMapper.toNearByJobResponse(
                jobRepository.findById(jobId).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND)));
    }

    public void askingJob (Client2JobRequest request) {
        Member worker = userAccessUtil.getMember();
        JobWithOwnerAndErrorCodeResponse jobData = changeJobStatusDsl.findJowWithOwnerAndErrorCode(
            worker.getId(), request.jobId()).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
        Optional.ofNullable(jobData.errorcode()).ifPresent(errorCode -> {throw new GlobalException(errorCode);});

        Matching matching = Matching.builder().job(jobData.job()).member(worker).status(MatchingStatus.ATTENDER).build();
        matchingRepository.save(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmUtil.makeRequestingJobBody(worker.getNickname(), jobData.job().getTitle())).build());
    }

    public void startJob (Client2JobRequest request) {
        Member worker = userAccessUtil.getMember();
        JobWithOwnerReponse jobData = changeJobStatusDsl.startJob(worker.getId(), request.jobId());
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 시작 알림!").body(
            fcmUtil.makeStartingJobBody(worker.getNickname(), jobData.job().getTitle())).build());
    }
}
