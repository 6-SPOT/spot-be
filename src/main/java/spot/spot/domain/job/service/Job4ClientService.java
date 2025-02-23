package spot.spot.domain.job.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.AttenderResponse;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.mapper.Job4ClientMapper;
import spot.spot.domain.job.repository.dsl.WorkerSearchingQuerydsl;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.job.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.mapper.MemberMapper;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Service
@RequiredArgsConstructor
public class Job4ClientService {
    private final UserAccessUtil userAccessUtil;
    private final JobUtil jobUtil;
    private final Job4ClientMapper job4ClientMapper;
    private final MemberMapper memberMapper;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final JobRepository jobRepository;
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    private final WorkerSearchingQuerydsl workerSearchingQuerydsl;

    public void registerJob(RegisterJobRequest request, MultipartFile file ) {
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
        return memberMapper.toDtoList(memberRepository.findWorkersNearByMember(lat, lng, jobUtil.convertZoomToRadius(zoomLevel)));
    }

    public Slice<AttenderResponse> findJobAttenderList(long jobId, Pageable pageable) {
        Slice<Worker> workers = workerSearchingQuerydsl.findWorkersByJobIdAndStatus(jobId, pageable);
        List<AttenderResponse> responseList = job4ClientMapper.toResponseList(workers.getContent());
        return new SliceImpl<>(responseList, pageable, workers.hasNext());
    }
}
