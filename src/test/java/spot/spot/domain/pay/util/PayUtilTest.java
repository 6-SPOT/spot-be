package spot.spot.domain.pay.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.service.ClientCommandService;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.repository.jpa.MatchingRepository;
import spot.spot.domain.job.query.service.ClientQueryService;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponse;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponse;
import spot.spot.domain.pay.repository.KlayAboutJobRepository;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.domain.pay.service.PayAPIRequestService;
import spot.spot.domain.pay.service.PayService;
import spot.spot.global.config.FireBaseConfig;
import spot.spot.global.security.util.UserAccessUtil;
import spot.spot.global.util.AwsS3ObjectStorage;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
@WithMockUser("1")
class PayUtilTest {

    @Autowired
    PayUtil payUtil;

    @Autowired
    PayService payService;

    @Autowired
    ClientQueryService clientQueryService;

    @Autowired
    ClientCommandService clientCommandService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    MatchingRepository matchingRepository;

    @MockitoBean
    AwsS3ObjectStorage awsS3ObjectStorage;

    @MockitoBean
    PayAPIRequestService payAPIRequestService;

    @Autowired
    KlayAboutJobRepository klayAboutJobRepository;

    @Autowired
    PayHistoryRepository payHistoryRepository;

    @MockitoBean
    UserAccessUtil userAccessUtil;

    @BeforeEach
    void before() {
        matchingRepository.deleteAllInBatch();
        klayAboutJobRepository.deleteAllInBatch();
        payHistoryRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("결제 준비를 하면 스케쥴러에 적용된다.")
    @Test
    void payReadyWithScheduler(){
        ///given
        MemberRequest.register register = MemberRequest.register.builder()
                .email("email")
                .nickname("nickname")
                .build();
        memberService.register(register);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );
        Member findMember = memberService.findByNickname("nickname");
        when(userAccessUtil.getMember()).thenReturn(findMember);
        RegisterJobRequest request = new RegisterJobRequest("title", "content", 10000, 0, 12.1111, 12.1111);
        RegisterJobResponse registerJobResponse = clientCommandService.registerJob(request, file);
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

        Job findJob = clientQueryService.findById(registerJobResponse.jobId());
        ///when
        payService.payReady(String.valueOf(findMember.getId()), "content", 10000, 0, findJob);
        PayHistory findPayHistory = payService.findByJobWithDepositor(findJob, findMember.getNickname());

        ///then
        Assertions.assertThat(payUtil.getScheduledTasks().containsKey(findPayHistory.getId())).isTrue();
        Assertions.assertThat(payUtil.getScheduledTasks().get(findPayHistory.getId()).isCancelled()).isFalse();
        payUtil.getScheduledTasks().get(findPayHistory.getId()).cancel(false);
    }

    @DisplayName("결제 승인을 하면 스케쥴러에서 삭제된다.")
    @Test
    void payApproveWithScheduler(){
        ///given
        MemberRequest.register register = MemberRequest.register.builder()
                .email("email")
                .nickname("nickname")
                .build();
        memberService.register(register);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );

        Member findMember = memberService.findByNickname("nickname");
        when(userAccessUtil.getMember()).thenReturn(findMember);
        RegisterJobRequest request = new RegisterJobRequest("title", "content", 10000, 0, 12.1111, 12.1111);
        RegisterJobResponse registerJobResponse = clientCommandService.registerJob(request, file);

        Job findJob = clientQueryService.findById(registerJobResponse.jobId());
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

        payService.payReady(String.valueOf(findMember.getId()), "content", 10000, 0, findJob);
        PayHistory findPayHistory = payService.findByJobWithDepositor(findJob, findMember.getNickname());
        PayApproveResponse payApproveResponse = new PayApproveResponse();
        PayApproveResponse.Amount amount = new PayApproveResponse.Amount();
        PayApproveResponse.Amount mockAmount = amount.create(10000, 100);
        String mockPGToken = "mockPgToken";
        PayApproveResponse mockPayApproveResponse = payApproveResponse.create(mockTid, "ORDER12345", "testUser", mockAmount, findJob.getContent());
        when(payAPIRequestService.payAPIRequest(
                eq("approve"),
                any(HttpEntity.class),
                eq(PayApproveResponse.class)
        )).thenReturn(mockPayApproveResponse);
        ///when
        payService.payApprove(String.valueOf(findMember.getId()), findJob, mockPGToken, 10000);

        ///then
        Assertions.assertThat(payUtil.getScheduledTasks().containsKey(findPayHistory.getId())).isFalse();
    }
}