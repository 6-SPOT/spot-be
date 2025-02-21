package spot.spot.domain.job.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.mapper.Job4ClientMapper;
import spot.spot.domain.job.repository.JobRepository;
import spot.spot.domain.job.repository.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.mapper.MemberMapper;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Service
@RequiredArgsConstructor
public class Job4ClientService {
    private final UserAccessUtil userAccessUtil;
    private final Job4ClientMapper job4ClientMapper;
    private final MemberMapper memberMapper;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final JobRepository jobRepository;
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;

    public void registerJob(RegisterJobRequest request, MultipartFile file ) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Job newJob = jobRepository.save(job4ClientMapper.registerRequestToJob(url, request));
        Member client = userAccessUtil.getMember();
        Matching matching = Matching.builder()
            .member(client)
            .job(newJob)
            .role(0)
            .isDone(false)
            .build();
        matchingRepository.save(matching);
    }

    public List<NearByWorkersResponse> findNearByWorkers(double lat, double lng, int zoomLevel) {
        return memberMapper.toDtoList(memberRepository.findWorkersNearByMember(lat, lng, convertZoomToRadius(zoomLevel)));
    }

    private double convertZoomToRadius(int zoom_level) {
        return switch (zoom_level) {
            case 21 -> 0.05;
            case 20 -> 0.1;
            case 19 -> 0.2;
            case 18 -> 0.5;
            case 17 -> 1;
            case 16 -> 2;
            case 15 -> 5;
            case 14 -> 10;
            case 13 -> 20;
            case 12 -> 50;
            default -> 100;
        };
    }


}
