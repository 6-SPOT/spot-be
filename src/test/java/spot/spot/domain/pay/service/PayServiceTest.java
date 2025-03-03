package spot.spot.domain.pay.service;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.repository.dsl.MatchingDsl;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.KlayAboutJob;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.response.*;
import spot.spot.domain.pay.repository.KlayAboutJobRepository;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.global.klaytn.ConnectToKlaytnNetwork;
import spot.spot.global.klaytn.api.ExchangeRateByBithumbApi;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class PayServiceTest {

    @Autowired
    private PayService payService;

    @MockitoBean
    private PayAPIRequestService payAPIRequestService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private KlayAboutJobRepository klayAboutJobRepository;

    @MockitoBean
    private ExchangeRateByBithumbApi exchangeRateByBithumbApi;

    @MockitoBean
    private ConnectToKlaytnNetwork connectToKlaytnNetwork;

    @MockitoBean
    private MatchingDsl matchingDsl;

    @Autowired
    private PayHistoryRepository payHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카카오페이 결제 준비API를 호출하면 tid, pc_url, mobile_url 값이 반환된다.")
    void payReady() {
        ///given
        // payReadyResponse Mock 데이터 설정
        String mockTid = "T1234ABCD5678";
        String mockPcUrl = "https://kakaopay-mock.com/pc";
        String mockMobileUrl = "https://kakaopay-mock.com/mobile";
        PayReadyResponse payReadyResponse = new PayReadyResponse();
        PayReadyResponse mockPayReadyResponse = payReadyResponse.create(mockTid, mockPcUrl, mockMobileUrl);

        // Mock 객체로 실제 API 호출을 대체
        when(payAPIRequestService.payAPIRequest(
                eq("ready"),
                any(HttpEntity.class),
                eq(PayReadyResponse.class)
        )).thenReturn(mockPayReadyResponse);

        ///when
        PayReadyResponseDto result = payService.payReady("testUser", "음쓰 버려주실 분~", 10000, 500);

        ///then
        Assertions.assertThat(result).isNotNull()
                 .extracting("tid", "redirectPCUrl", "redirectMobileUrl")
                 .containsExactly("T1234ABCD5678","https://kakaopay-mock.com/pc", "https://kakaopay-mock.com/mobile");
    }

    @Test
    @DisplayName("카카오페이 결제 승인API를 호출하면 구매자, 일 타이틀, 금액 값이 반환된다.")
    void payApprove() {
        ///given
        String mockPGToken = "mockPgToken";
        PayApproveResponse.Amount amount = new PayApproveResponse.Amount();
        int mockPrice = 1000;
        Member mockMember = createMockMember();
        PayApproveResponse.Amount mockAmount = amount.create(mockPrice, 100);
        PayHistory mockPayHistory = PayHistory.builder().id(1L).build();

        PayApproveResponse payApproveResponse = new PayApproveResponse();
        Job mockJob = createMockJob(mockPayHistory, "T123141515");

        // ApproveResponse mock 데이터 생성
        String mockTid = "T1234ABCD5678";
        PayApproveResponse mockPayApproveResponse = payApproveResponse.create(mockTid, "ORDER12345", "testUser", mockAmount, mockJob.getContent());
        SingleKeyring mockSingleKeyring = mock(SingleKeyring.class);
        when(mockSingleKeyring.getAddress()).thenReturn("0x123456789abcdef");
        when(memberService.findById(anyString())).thenReturn(mockMember);
        when(matchingDsl.findWorkerNicknameByJob(any(Job.class))).thenReturn(Optional.of("testWorker"));
        when(exchangeRateByBithumbApi.exchangeToKaia(anyInt())).thenReturn(1.2);
        when(connectToKlaytnNetwork.getSingleKeyring()).thenReturn(mockSingleKeyring);
        when(klayAboutJobRepository.save(any(KlayAboutJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock 객체로 실제 API 호출을 대체
        when(payAPIRequestService.payAPIRequest(
                eq("approve"),
                any(HttpEntity.class),
                eq(PayApproveResponse.class)
        )).thenReturn(mockPayApproveResponse);

        ///when
        PayApproveResponseDto result = payService.payApprove("1", mockJob, mockPGToken, mockPrice);

        ///then
        Assertions.assertThat(result).isNotNull()
                .extracting("nickname", "content", "amount")
                .containsExactly("testUser", mockJob.getContent(), mockPrice);
    }

    @Test
    @DisplayName("카카오페이 주문 조회API를 호출하면 구매자, 판매자, 금액, 일타이틀 값이 반환된다.")
    void payOrder() {
        ///given
        //payOrderResponse mock 생성
        String mockTid = "T1234ABCD5678";
        Member mockMember = createMockMember();
        PayOrderResponse payOrderResponse = new PayOrderResponse();
        String mockDomain = "spot";
        PayOrderResponse mockPayOrderResponse = payOrderResponse.create(mockMember.getNickname(), mockDomain, 10000, "음쓰 버려주실 분~");

        when(payAPIRequestService.payAPIRequest(
                eq("order"),
                any(HttpEntity.class),
                eq(PayOrderResponse.class)
        )).thenReturn(mockPayOrderResponse);

        ///when
        PayOrderResponseDto result = payService.payOrder(mockTid);

        ///then
        Assertions.assertThat(result).isNotNull()
                .extracting("nickname", "domain", "amount", "content")
                .containsExactly(mockMember.getNickname(), mockDomain, 10000, "음쓰 버려주실 분~");
    }

    @Test
    @DisplayName("카카오페이 주문 취소API를 호출하면 tid와 해당하는 일의 금액과 해당하는 일의 취소금액이 반환된다.")
    void payCancel() {
        ///given
        String mockTid = "T7bf22de40f539f479e6";
        int amount = 10000;
        int cancelAmount = 10000;
        //mock 데이터
        Member mockMember = createMockMember();
        PayHistory mockPayHistory = createMockPayHistory(mockMember.getNickname());
        Job mockJob = createMockJob(mockPayHistory, "T123145151");
        KlayAboutJob mockKlayAboutJob = createMockKlayAboutJob(amount, mockJob);

        // payCancelResponse mock 생성
        PayCancelResponse payCancelResponse = new PayCancelResponse();
        PayCancelResponse.Amount payAmount = new PayCancelResponse.Amount();
        PayCancelResponse.Amount totalPayAmount = payAmount.create(10000, 0);
        PayCancelResponse.Amount cancelPayAmount = payAmount.create(10000, 0);
        String mockDomain = "spot";
        PayCancelResponse mockPayCancel = payCancelResponse.create(mockMember.getNickname(), mockDomain, totalPayAmount, cancelPayAmount);

        //메서드에 필요한 mock 반환값 설정
        SingleKeyring mockSingleKeyring = mock(SingleKeyring.class);
        when(mockSingleKeyring.getAddress()).thenReturn("0x123456789abcdef");
        when(matchingDsl.findWorkerNicknameByJob(any(Job.class))).thenReturn(Optional.of("testWorker"));
        when(memberService.findMemberByIdOrNickname(any(), any())).thenReturn(mockMember);
        when(exchangeRateByBithumbApi.exchangeToKaia(anyInt())).thenReturn(1.2);
        when(connectToKlaytnNetwork.getSingleKeyring()).thenReturn(mockSingleKeyring);
        when(klayAboutJobRepository.save(any(KlayAboutJob.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(klayAboutJobRepository.findByJob(any(Job.class))).thenReturn(Optional.of(mockKlayAboutJob));

        when(payAPIRequestService.payAPIRequest(
                eq("cancel"),
                any(HttpEntity.class),
                eq(PayCancelResponse.class)
        )).thenReturn(mockPayCancel);

        ///when
        PayCancelResponseDto result = payService.payCancel(mockJob, amount);

        ///then
        Assertions.assertThat(result).isNotNull()
                .extracting("nickname", "domain", "amount", "cancelAmount")
                .containsExactly(mockMember.getNickname(), mockDomain, totalPayAmount.getTotal(), cancelPayAmount.getTotal());
    }

    @Test
    @DisplayName("일 완료시 일에 대한 금액을 해결사에게 point로 반환한다.")
    void payTransfer() {
        ///given
        //mock Data
        String mockTid = "T12351612425";
        int mockAmount = 10000;
        Member mockMember = createMockMember();
        PayHistory mockPayHistory = createMockPayHistory(mockMember.getNickname());
        Job mockJob = createMockJob(mockPayHistory, mockTid);
        KlayAboutJob mockKlayAboutJob = createMockKlayAboutJob(mockAmount, mockJob);
        SingleKeyring mockSingleKeyring = mock(SingleKeyring.class);

        when(mockSingleKeyring.getAddress()).thenReturn("tx1231415116t16");
        when(memberService.findMemberByIdOrNickname(any(), any())).thenReturn(mockMember);
        when(klayAboutJobRepository.findByJob(any(Job.class))).thenReturn(Optional.of(mockKlayAboutJob));
        when(connectToKlaytnNetwork.getSingleKeyring()).thenReturn(mockSingleKeyring);

        ///when
        PaySuccessResponseDto result = payService.payTransfer("1", mockAmount, mockJob);

        ///then result의 반환값과 일 금액 + 유저 기존금액 비교
        Assertions.assertThat(result).isNotNull()
                .extracting("totalPointAmount")
                .isEqualTo(mockAmount + mockMember.getPoint());
    }

    @Test
    @DisplayName("일 등록 시 멤버의 결제 내역을 저장한다.")
    void savePayHistoryByReady() {
        ///given
        String depositor = "testUser";
        int mockAmount = 1000;
        int mockPoint = 100;

        PayHistory payHistory = payService.savePayHistory(depositor, mockAmount, mockPoint);

        ///when
        PayHistory findPayHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        ///then
        assertEquals(payHistory, findPayHistory);
    }

    @Test
    @DisplayName("결제 승인 시 해당일에 대한 멤버의 결재내역에서 결제상태를 진행중으로 변경한다.")
    void updatePayHistoryByApprove() {
        ///given
        String depositor = "testUser2";
        //payHistory 가상 데이터
        PayHistory mockPayHistory = createMockPayHistory(depositor);
        //payHistory 저장
        payHistoryRepository.save(mockPayHistory);

        PayHistory payHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        ///when
        payService.updatePayHistory(payHistory, PayStatus.PROCESS, "testUser2");

        ///then
        assertEquals(payHistory.getPayStatus(), PayStatus.PROCESS);
    }

    @Test
    @DisplayName("결제 취소 시 해당일에 대한 멤버의 결제내역에서 결제상태를 실패로 변경한다.")
    void updatePayHistoryByCancel() {
        ///given
        String depositor = "testUser2";
        //payHistory 가상 데이터
        PayHistory mockPayHistory = createMockPayHistory(depositor);

        //payHistory 저장
        payHistoryRepository.save(mockPayHistory);

        PayHistory payHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        ///when
        payService.updatePayHistory(payHistory, PayStatus.FAIL, "testUser2");

        ///then
        assertEquals(payHistory.getPayStatus(), PayStatus.FAIL);
    }

    @Test
    @DisplayName("일 완료 시 해당일에 대한 멤버의 결제내역에서 결제상태를 성공으로 변경한다.")
    void updatePayHistoryBySuccess() {
        ///given
        String depositor = "testUser2";
        //payHistory 가상 데이터
        PayHistory mockPayHistory = createMockPayHistory(depositor);
        //payHistory 저장
        payHistoryRepository.save(mockPayHistory);

        PayHistory payHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        ///when
        payService.updatePayHistory(payHistory, PayStatus.SUCCESS, "testUser2");

        ///then
        assertEquals(payHistory.getPayStatus(), PayStatus.SUCCESS);
    }

    private static Job createMockJob(PayHistory mockPayHistory, String mockTid) {
        return Job.builder().title("음쓰 버려주실 분~")
                .content("음쓰 버려주실 분~")
                .id(1L)
                .tid(mockTid)
                .payment(mockPayHistory)
                .build();
    }

    private PayHistory createMockPayHistory(String depositor) {
        return PayHistory
                .builder()
                .worker("worker")
                .payStatus(PayStatus.PENDING)
                .depositor(depositor).build();
    }

    private static Member createMockMember() {
        Member mockMember = mock(Member.class);
        when(mockMember.getNickname()).thenReturn("testUser1");
        when(mockMember.getId()).thenReturn(1L);
        when(mockMember.getEmail()).thenReturn("test@test.com");
        when(mockMember.getPoint()).thenReturn(1000);
        return mockMember;
    }

    private static KlayAboutJob createMockKlayAboutJob(int mockAmount, Job mockJob) {
        return KlayAboutJob.builder()
                .amtKlay(1.2) // 가상 카이아 값
                .amtKrw(mockAmount)
                .exchangeRate(1.2)
                .job(mockJob)
                .build();
    }
}