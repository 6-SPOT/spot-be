package spot.spot.domain.job.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.Job4WorkerRequest;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.AttenderResponse;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.mapper.Job4ClientMapper;
import spot.spot.domain.job.repository.dsl.ChangeJobStatusDsl;
import spot.spot.domain.job.repository.dsl.SearchingListDsl;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.job.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.mapper.MemberMapper;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.notification.dto.response.FcmDTO;
import spot.spot.domain.notification.service.FcmUtil;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Service
@RequiredArgsConstructor
public class Job4ClientService {
    // util
    private final UserAccessUtil userAccessUtil;
    private final JobUtil jobUtil;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    // mapper
    private final Job4ClientMapper job4ClientMapper;
    private final MemberMapper memberMapper;
    // jpa repo
    private final JobRepository jobRepository;
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    // query dsl
    private final SearchingListDsl searchingListDsl;
    private final ChangeJobStatusDsl changeJobStatusDsl;
    private final FcmUtil fcmUtil;

    public void registerJob(RegisterJobRequest request, MultipartFile file) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Job newJob = jobRepository.save(job4ClientMapper.registerRequestToJob(url, request));
        Member client = userAccessUtil.getMember();
        Matching matching = Matching.builder()
            .member(client)
            .job(newJob)
            .status(MatchingStatus.OWNER)
            .build();
        matchingRepository.save(matching);
    }

    public List<NearByWorkersResponse> findNearByWorkers(double lat, double lng, int zoomLevel) {
        return memberMapper.toDtoList(memberRepository
            .findWorkersNearByMember(lat, lng, jobUtil.convertZoomToRadius(zoomLevel)));
    }

    @Transactional(readOnly = true)
    public Slice<AttenderResponse> findJobAttenderList(long jobId, Pageable pageable) {
        Slice<Worker> workers = searchingListDsl.findWorkersByJobIdAndStatus(jobId, pageable);
        List<AttenderResponse> responseList = job4ClientMapper.toResponseList(workers.getContent());
        return new SliceImpl<>(responseList, pageable, workers.hasNext());
    }

    public void askingJob2Worker (Job4WorkerRequest request) {
        Member worker = memberRepository
            .findById(request.attendeId()).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusDsl.findJobWithValidation(request.attendeId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.ATTENDER).build();
        matchingRepository.save(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmUtil.askRequest2WorkerMsg(worker.getNickname(), job.getTitle())).build());
    }
}
