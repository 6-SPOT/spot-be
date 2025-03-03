package spot.spot.domain.job.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.Job2ClientRequest;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.request.YesOrNo2WorkersRequest;
import spot.spot.domain.job.dto.response.AttenderResponse;
import spot.spot.domain.job.dto.response.JobResponse;
import spot.spot.domain.job.dto.response.JobSituationResponse;
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
import spot.spot.domain.member.repository.MemberQueryRepository;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.notification.dto.response.FcmDTO;
import spot.spot.domain.notification.service.FcmUtil;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponseDto;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class Job4ClientService {
    // Util
    private final JobUtil jobUtil;
    private final UserAccessUtil userAccessUtil;
    private final AwsS3ObjectStorage awsS3ObjectStorage;
    // Mapper
    private final Job4ClientMapper job4ClientMapper;
    private final MemberMapper memberMapper;
    private final JobRepository jobRepository;
    // JPA
    private final MatchingRepository matchingRepository;
    private final MemberQueryRepository memberQueryRepository;
    // Query dsl
    private final SearchingListDsl searchingListDsl;
    private final ChangeJobStatusDsl changeJobStatusDsl;
    private final FcmUtil fcmUtil;
    private final PayService payService;
    private final MemberRepository memberRepository;

    /*
    *               [ 목차 ]
    *       1. 일 등록 서비스
    *       2. 근처 해결사 라스트 불러오기
    *       3. 일 신청자 리스트 불러오기
    *       4. Worker에게 일 의뢰하기
    *       5. Worker의 일 신청 수락 혹은 거절하기
    *       6. Worker의 일 철회 요청
    *       7. 결제 내역으로 일 찾기
    *       8. 일 하나 상세 확인하기
    * */

    // 1) 일 등록
    public PayReadyResponseDto registerJob(RegisterJobRequest request, MultipartFile file) {
        String url = awsS3ObjectStorage.uploadFile(file);
        Member client = userAccessUtil.getMember();
        PayReadyResponseDto payReadyResponseDto = payService.payReady(client.getNickname(), request.content(), request.money(), request.point());
        String tid = payReadyResponseDto.tid();
        PayHistory payHistory = payReadyResponseDto.payHistory();
        Job newJob = jobRepository.save(job4ClientMapper.registerRequestToJob(url, request, tid, payHistory));

        Matching matching = Matching.builder()
            .member(client)
            .job(newJob)
            .status(MatchingStatus.OWNER)
            .build();
        matchingRepository.save(matching);
        return payReadyResponseDto;
    }
    // 2) 근처 해결사 찾기
    public List<NearByWorkersResponse> findNearByWorkers(double lat, double lng, int zoomLevel) {
        return memberMapper.toDtoList(memberQueryRepository.findWorkerNearByMember(lat, lng, jobUtil.convertZoomToRadius(zoomLevel)));
    }
    // 3) 신청자 리스트 찾기
    @Transactional(readOnly = true)
    public Slice<AttenderResponse> findJobAttenderList(long jobId, Pageable pageable) {
        Slice<Worker> workers = searchingListDsl.findWorkersByJobIdAndStatus(jobId, pageable);
        List<AttenderResponse> responseList = job4ClientMapper.toResponseList(workers.getContent());
        return new SliceImpl<>(responseList, pageable, workers.hasNext());
    }
    // 4) 해결사에게 일 의뢰
    public void askingJob2Worker (Job2ClientRequest request) {
        Member worker = memberRepository
            .findById(request.attenderId()).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusDsl.findJobWithValidation(request.attenderId(), request.jobId());
        Matching matching = Matching.builder().job(job).member(worker).status(MatchingStatus.REQUEST).build();
        matchingRepository.save(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 해결 신청 알림!").body(
            fcmUtil.askRequest2WorkerMsg(worker.getNickname(), job.getTitle())).build());
    }
    // 5) 일 수락 혹은 거절
    @Transactional
    public void yesOrNo2RequestOfWorker(YesOrNo2WorkersRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository
            .findById(request.attenderId()).orElseThrow(() -> new GlobalException(
            ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.ATTENDER);
        changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), request.isYes()? MatchingStatus.YES : MatchingStatus.NO);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("일 시작 알림!").body(
            fcmUtil.requestAcceptedBody(owner.getNickname(), worker.getNickname(), job.getTitle())).build());
    }
    // 6) NO Show 사용자를 위해 일 철회 요청
    @Transactional
    public void requestWithdrawal(Job2ClientRequest request) {
        Member owner = userAccessUtil.getMember();
        Member worker = memberRepository
            .findById(request.attenderId()).orElseThrow(() -> new GlobalException(
                ErrorCode.MEMBER_NOT_FOUND));
        Job job = changeJobStatusDsl.findJobWithValidation(worker.getId(), request.jobId(), MatchingStatus.START);
        int payAmountByJob = payService.getPayAmountByJob(job);
        payService.payCancel(job, payAmountByJob);
        Matching matching = changeJobStatusDsl.updateMatchingStatus(worker.getId(), request.jobId(), MatchingStatus.SLEEP);
        jobUtil.scheduledSleepMatching2Cancel(matching);
        fcmUtil.singleFcmSend(worker.getId(), FcmDTO.builder().title("혹시 잠수 타셨나요??").body(
            fcmUtil.requestAcceptedBody(owner.getNickname(), worker.getNickname(), job.getTitle())).build());
    }
    // 7) 결제 내역으로 일 찾기
    public Job findByTid(String tid) {
        return jobRepository.findByTid(tid).orElseThrow(() -> new GlobalException(ErrorCode.INVALID_TITLE));
    }
    // 8) 일 하나 상세 보기
    public JobResponse getJobDetail(long jobId) {
        return  null;
    }
    // 9) 내가 맡긴 일 현황 보기
    public List<JobSituationResponse> getSituationsByOwner() {
        Member owner = userAccessUtil.getMember();
        return searchingListDsl.findJobSituationsByOwner(owner.getId());
    }
}
