package spot.spot.domain.pay.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.repository.jpa.CertificationRepository;
import spot.spot.domain.job.command.service.ClientCommandService;
import spot.spot.domain.job.command.service.WorkerCommandService;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.job.query.service.ClientQueryService;
import spot.spot.domain.member.entity.AbilityType;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponse;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponse;
import spot.spot.domain.pay.service.PayAPIRequestService;
import spot.spot.domain.pay.service.PayService;
import spot.spot.domain.pay.util.PayUtil;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.util.AwsS3ObjectStorage;

import java.util.List;

import static org.mockito.BDDMockito.*;

@SpringBootTest
@WithMockUser(username = "1")
@Transactional
@ActiveProfiles("local")
@Slf4j
class PayRepositoryDslTest {

    @Autowired
    PayRepositoryDsl payRepositoryDsl;

    @Autowired
    ClientCommandService clientCommandService;

    @Autowired
    ClientQueryService clientQueryService;

    @Autowired
    WorkerCommandService workerCommandService;

    @Autowired
    PayService payService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MatchingRepository matchingRepository;

    @Autowired
    CertificationRepository certificationRepository;

    @MockitoBean
    AwsS3ObjectStorage awsS3ObjectStorage;

    @MockitoBean
    PayAPIRequestService payAPIRequestService;

    @MockitoBean
    PayUtil payUtil;

    @DisplayName("매칭정보로 해당하는 일의 가격 정보를 조회할 수 있다.")
    @Test
    void researchAmount(){
        ///given
        int validAmount = 1000;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );

        Member depositor = Member.builder()
                .email("depositor@test")
                .nickname("depositor")
                .build();

        Member worker = Member.builder()
                .email("worker@test")
                .nickname("worker")
                .build();

        Member worker2 = Member.builder()
                .email("worker@test")
                .nickname("worker2")
                .build();

        Member saveDepositor = memberRepository.save(depositor);
        Member saveWorker = memberRepository.save(worker);
        Member saveWorker2 = memberRepository.save(worker2);

        RegisterJobRequest request = new RegisterJobRequest("title", "content", validAmount, 0, 12.1111, 12.1111);
        given(awsS3ObjectStorage.uploadFile(file))
                .willReturn("https://s3-bucket.com/test-file.txt");
        doNothing()
                .when(payUtil)
                .insertFromSchedule(any());

        String mockTid = "T1234ABCD5678";
        String mockPcUrl = "https://kakaopay-mock.com/pc";
        String mockMobileUrl = "https://kakaopay-mock.com/mobile";
        PayReadyResponse payReadyResponse = new PayReadyResponse();
        PayReadyResponse mockPayReadyResponse = payReadyResponse.create(mockTid, mockPcUrl, mockMobileUrl);
        when(payAPIRequestService.payAPIRequest(
                eq("ready"),
                any(HttpEntity.class),
                eq(PayReadyResponse.class)
        )).thenReturn(mockPayReadyResponse);

        RegisterJobResponse registerJobResponse = clientCommandService.registerJob(request, file);
        Job findJob = clientQueryService.findById(registerJobResponse.jobId());
        payService.payReady(String.valueOf(saveDepositor.getId()), request.content(), request.money(), request.point(), findJob);
        PayHistory findPayHistory = payService.findByJob(findJob);
        findPayHistory.setWorker(saveWorker.getNickname());

        Matching matching = matchingRepository.findByMemberAndJob_Id(saveDepositor, findJob.getId()).orElseThrow(() -> new GlobalException(ErrorCode.MATCHING_NOT_FOUND));
        Matching matching2 = Matching.builder().job(findJob).member(saveWorker).build();
        Matching matching3 = Matching.builder().job(findJob).member(saveWorker2).build();
        matchingRepository.save(matching2);
        matchingRepository.save(matching3);

        ///when
        Integer amount = payRepositoryDsl.findByPayAmountFromMatchingJob(matching2.getId(), saveWorker.getId());

        ///then
        Assertions.assertThat(amount).isEqualTo(validAmount);
    }

}