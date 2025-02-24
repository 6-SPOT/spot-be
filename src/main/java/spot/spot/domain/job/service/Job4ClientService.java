package spot.spot.domain.job.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import spot.spot.domain.job.repository.dsl.SearchingListDsl;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.job.repository.jpa.MatchingRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.mapper.MemberMapper;
import spot.spot.domain.member.repository.MemberQueryRepository;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.dto.PayReadyResponseDto;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class Job4ClientService {
    private final UserAccessUtil userAccessUtil;
    private final Job4ClientMapper job4ClientMapper;
    private final MemberMapper memberMapper;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    private final JobRepository jobRepository;
    private final MatchingRepository matchingRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final JobUtil jobUtil;
    // query dsl
    private final SearchingListDsl searchingListDsl;
    private final MemberService memberService;
    private final PayService payService;

    public PayReadyResponseDto registerJob(RegisterJobRequest request,MultipartFile file) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Member client = userAccessUtil.getMember();
        PayReadyResponseDto payReadyResponseDto = payService.payReady(client.getNickname(), request.title(), request.money(), request.point());
        String tid = payReadyResponseDto.tid();
        PayHistory payHistory = payReadyResponseDto.payHistory();

        log.info("redirect_pc_url = {}, redirect_mobile_url = {}", payReadyResponseDto.redirectPCUrl(), payReadyResponseDto.redirectMobileUrl());
        Job newJob = jobRepository.save(job4ClientMapper.registerRequestToJob(url, request, tid, payHistory));

        Matching matching = Matching.builder()
            .member(client)
            .job(newJob)
            .status(MatchingStatus.OWNER)
            .build();
        matchingRepository.save(matching);
        return payReadyResponseDto;
    }

    public List<NearByWorkersResponse> findNearByWorkers(double lat, double lng, int zoomLevel) {
        return memberMapper.toDtoList(memberQueryRepository.findWorkerNearByMember(lat, lng, jobUtil.convertZoomToRadius(zoomLevel)));
    }

    public Slice<AttenderResponse> findJobAttenderList(long jobId, Pageable pageable) {
        Slice<Worker> workers = searchingListDsl.findWorkersByJobIdAndStatus(jobId, pageable);
        List<AttenderResponse> responseList = job4ClientMapper.toResponseList(workers.getContent());
        return new SliceImpl<>(responseList, pageable, workers.hasNext());
    }

    public Job findByTid(String jobTitle) {
        return jobRepository.findByTitle(jobTitle).orElseThrow(() -> new GlobalException(ErrorCode.INVALID_TITLE));
    }
}
