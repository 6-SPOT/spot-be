package spot.spot.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.job.mapper.Job4ClientMapper;
import spot.spot.domain.job.repository.JobRepository;
import spot.spot.domain.job.repository.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Service
@RequiredArgsConstructor
public class Job4ClientService {
    private final UserAccessUtil userAccessUtil;
    private final Job4ClientMapper job4ClientMapper;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final JobRepository jobRepository;
    private final MatchingRepository matchingRepository;

    public void registerJob(RegisterJobRequest request, MultipartFile file ) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Job newJob = jobRepository.save(job4ClientMapper.registerRequestToJob(url, request));
        Member client = userAccessUtil.getMember().orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        Matching matching = Matching.builder()
            .member(client)
            .job(newJob)
            .role(0)
            .isDone(false)
            .build();
        matchingRepository.save(matching);
    }
}
