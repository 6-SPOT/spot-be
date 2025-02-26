package spot.spot.domain.pay.service;

import com.klaytn.caver.wallet.keyring.SingleKeyring;
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

import java.util.List;
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

    @MockitoBean // ✅ @MockBean 사용
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
    @DisplayName("카카오페이 결제 준비API Mock 테스트")
    void payReady() {
        // payReadyResponse Mock 데이터 설정
        String mockTid = "T1234ABCD5678";
        String mock_setNext_redirect_pc_url = "https://kakaopay-mock.com/pc";
        String mock_setNext_redirect_mobile_url = "https://kakaopay-mock.com/mobile";
        PayReadyResponse mockResponse = new PayReadyResponse();
        mockResponse.setTid(mockTid);
        mockResponse.setNext_redirect_pc_url(mock_setNext_redirect_pc_url);
        mockResponse.setNext_redirect_mobile_url(mock_setNext_redirect_mobile_url);
        mockResponse.setNext_redirect_app_url("https://kakaopay-mock.com/app");
        mockResponse.setAndroid_app_scheme("kakaopay://payment");
        mockResponse.setIos_app_scheme("kakaopay://payment-ios");
        mockResponse.setCreated_at("2025-02-25T12:34:56");

        // Mock 객체로 실제 API 호출을 대체
        when(payAPIRequestService.payAPIRequest(
                eq("ready"),
                any(HttpEntity.class),
                eq(PayReadyResponse.class)
        )).thenReturn(mockResponse);

        // 결제 준비 실행
        PayReadyResponseDto result = payService.payReady("testUser", "음쓰 버려주실 분~", 10000, 500);

        // 결과 검증
        assertNotNull(result); //payReady 결과값 존재하는지
        assertEquals(mockTid, result.tid()); //실제 반환값이 설정한 tid 값과 같은가
        assertEquals(mock_setNext_redirect_pc_url, result.redirectPCUrl()); //실제 반환값이 설정한 redirect_pc_url 값과 같은가
        assertEquals(mock_setNext_redirect_mobile_url, result.redirectMobileUrl()); //실제 반환값이 설정한 redirect_mobile_url 값과 같은가
    }

    @Test
    @DisplayName("카카오페이 결제 승인API Mock 테스트")
    void payApprove() {
        // mockData
        String mockPGToken = "mockPgToken";
        int totalAmount = 10000;
        Member mockMember = Member.builder().nickname("testUser1")
                .email("test@test.com")
                .build();

        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();


        // ApproveResponse mock 데이터 생성
        PayApproveResponse mockApproveResponse = PayApproveResponse.builder()
                .aid("A1234567890")
                .tid("T1234ABCD5678")
                .cid("TC0ONETIME")
                .sid("S1234ABCDE")
                .partner_order_id("ORDER12345")
                .partner_user_id("testUser")
                .item_name("음쓰 버려주실 분~")
                .item_code("ITEM123")
                .payload("Mock payload data")
                .quantity(1)
                .amount(PayApproveResponse.Amount.builder()
                        .total(10000)
                        .tax_free(0)
                        .vat(1000)
                        .point(500)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .payment_method_type("CARD")
                .card_info(PayApproveResponse.CardInfo.builder()
                        .kakaopay_purchase_corp("비씨카드")
                        .kakaopay_purchase_corp_code("BC")
                        .kakaopay_issuer_corp("신한카드")
                        .kakaopay_issuer_corp_code("SH")
                        .bin("123456")
                        .card_type("신용카드")
                        .install_month("3")
                        .approved_id("APPROVED123")
                        .card_mid("MID12345")
                        .interest_free_install("N")
                        .card_item_code("ITEM12345")
                        .installment_type("할부 3개월")
                        .build())
                .created_at("2025-02-25T12:34:56")
                .approved_at("2025-02-25T12:35:10")
                .build();

        SingleKeyring mockSingleKeyring = mock(SingleKeyring.class);
        when(mockSingleKeyring.getAddress()).thenReturn("0x123456789abcdef");
        when(memberService.findById(anyLong())).thenReturn(mockMember);
        when(matchingDsl.findWorkerNicknameByJob(any(Job.class))).thenReturn(Optional.of("testWorker"));
        when(exchangeRateByBithumbApi.exchangeToKaia(anyInt())).thenReturn(1.2);
        when(connectToKlaytnNetwork.getSingleKeyring()).thenReturn(mockSingleKeyring);
        when(klayAboutJobRepository.save(any(KlayAboutJob.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Mock 객체로 실제 API 호출을 대체
        when(payAPIRequestService.payAPIRequest(
                eq("approve"),
                any(HttpEntity.class),
                eq(PayApproveResponse.class)
        )).thenReturn(mockApproveResponse);

        // 결제 승인 요청 실행
        PayApproveResponse result = payService.payApprove("1", mockJob, mockPGToken, totalAmount);

        // 검증
        assertNotNull(result); //결과 값이 존재하는지
        assertEquals(mockApproveResponse.getTid(), result.getTid()); //실제 반환값이 설정한 tid 값과 같은가
        assertEquals(mockApproveResponse.getAmount().getTotal(), result.getAmount().getTotal()); //실제 반환값이 설정한 금액 값과 같은가

        // payAPIRequestService가 제대로 호출되었는지 검증
        verify(payAPIRequestService, times(1)).payAPIRequest(anyString(), any(HttpEntity.class), eq(PayApproveResponse.class));
        verify(memberService, times(1)).findById(anyLong());
        verify(matchingDsl, times(1)).findWorkerNicknameByJob(any(Job.class));
        verify(klayAboutJobRepository, times(1)).save(any(KlayAboutJob.class));
    }

    @Test
    @DisplayName("카카오페이 주문 조회API Mock테스트")
    void payOrder() {
        //payOrderResponse mock 생성
        String mockTid = "T1234ABCD5678";
        String mockCid = "TC0ONETIME";
        PayOrderResponse mockPayOrderResponse = PayOrderResponse.builder()
                .tid(mockTid)
                .cid(mockCid)
                .status("SUCCESS")
                .partner_order_id("ORDER12345")
                .partner_user_id("testUser")
                .payment_method_type("CARD")
                .item_name("음쓰 버려주실 분~")
                .item_code("ITEM123")
                .quantity(1)
                .created_at("2025-02-25T12:30:00")
                .approved_at("2025-02-25T12:32:00")
                .canceled_at(null) // 취소되지 않음
                .amount(PayOrderResponse.Amount.builder()
                        .total(10000)
                        .tax_free(0)
                        .vat(1000)
                        .point(500)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .canceled_amount(PayOrderResponse.Amount.builder()
                        .total(0)
                        .tax_free(0)
                        .vat(0)
                        .point(0)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .cancel_available_amount(PayOrderResponse.Amount.builder()
                        .total(10000)
                        .tax_free(0)
                        .vat(1000)
                        .point(500)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .selected_card_info(PayOrderResponse.SelectedCardInfo.builder()
                        .card_bin("123456")
                        .card_type("신용카드")
                        .install_month("3")
                        .interest_free_install("N")
                        .build())
                .payment_action_details(List.of(
                        PayOrderResponse.PaymentActionDetail.builder()
                                .aid("A1234567890")
                                .approved_at("2025-02-25T12:32:00")
                                .point_amount(500)
                                .discount_amount(0)
                                .green_deposit_amount(0)
                                .payment_action_type("PAYMENT")
                                .build()
                ))
                .build();

        when(payAPIRequestService.payAPIRequest(
                eq("order"),
                any(HttpEntity.class),
                eq(PayOrderResponse.class)
        )).thenReturn(mockPayOrderResponse);

        PayOrderResponse result = payService.payOrder(mockTid);


        assertNotNull(result);
        assertEquals(mockTid, result.getTid());
        assertEquals(mockCid, result.getCid());
    }

    @Test
    @DisplayName("카카오페이 주문 취소API Mock테스트")
    void payCancel() {
        String mockTid = "T7bf22de40f539f479e6";
        int amount = 10000;
        //mock 데이터
        Member mockMember = Member.builder().nickname("testUser1")
                .email("test@test.com")
                .point(0)
                .build();

        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .money(amount)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();

        KlayAboutJob mockKlayAboutJob = KlayAboutJob.builder()
                .amtKlay(1.2) // 가상 카이아 값
                .amtKrw(amount)
                .exchangeRate(1.2)
                .job(mockJob)
                .payStatus(PayStatus.PROCESS)
                .build();

        // payCancelResponse mock 생성
        PayCancelResponse mockPayCancelResponse = PayCancelResponse.builder()
                .tid(mockTid)
                .cid("TC0ONETIME")
                .status("CANCEL_PAYMENT")
                .partner_order_id("ilmatch")
                .partner_user_id("테스트유저")
                .payment_method_type("MONEY")
                .aid("A7bf234840f539f479e7")
                .quantity(1)
                .amount(PayCancelResponse.Amount.builder()
                        .total(10000)
                        .tax_free(0)
                        .vat(0)
                        .point(0)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .canceled_amount(PayCancelResponse.Amount.builder()
                        .total(10000)
                        .tax_free(0)
                        .vat(0)
                        .point(0)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .cancel_available_amount(PayCancelResponse.Amount.builder()
                        .total(0)
                        .tax_free(0)
                        .vat(0)
                        .point(0)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .approved_cancel_amount(PayCancelResponse.Amount.builder()
                        .total(10000)
                        .tax_free(0)
                        .vat(0)
                        .point(0)
                        .discount(0)
                        .green_deposit(0)
                        .build())
                .created_at("2025-02-26T23:19:10")
                .approved_at("2025-02-26T23:20:17")
                .canceled_at("2025-02-26T23:20:56")
                .build();

        //메서드에 필요한 mock 반환값 설정
        SingleKeyring mockSingleKeyring = mock(SingleKeyring.class);
        when(mockSingleKeyring.getAddress()).thenReturn("0x123456789abcdef");
        when(memberService.findByNickname(anyString())).thenReturn(mockMember);
        when(matchingDsl.findWorkerNicknameByJob(any(Job.class))).thenReturn(Optional.of("testWorker"));
        when(exchangeRateByBithumbApi.exchangeToKaia(anyInt())).thenReturn(1.2);
        when(connectToKlaytnNetwork.getSingleKeyring()).thenReturn(mockSingleKeyring);
        when(klayAboutJobRepository.save(any(KlayAboutJob.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(klayAboutJobRepository.findByJob(any(Job.class))).thenReturn(Optional.of(mockKlayAboutJob));

        when(payAPIRequestService.payAPIRequest(
                eq("cancel"),
                any(HttpEntity.class),
                eq(PayCancelResponse.class)
        )).thenReturn(mockPayCancelResponse);

        PayCancelResponse result = payService.payCancel(mockJob, amount);

        assertNotNull(result);
        assertEquals(mockTid, result.getTid());
        assertEquals(amount, result.getAmount().getTotal());
        assertEquals(amount, result.getCanceled_amount().getTotal());
    }

    @Test
    @DisplayName("일 완료시 일에 대한 금액을 해결사에게 point로 반환")
    void payTransfer() {
        //mock Data
        String mockTid = "T12351612425";
        int mockAmount = 10000;
        Member mockMember = Member.builder()
                .nickname("testUser")
                .email("test@test.com")
                .id(1L)
                .build();
        PayHistory mockPayHistory = PayHistory.builder()
                .payStatus(PayStatus.SUCCESS)
                .payPoint(0)
                .payAmount(mockAmount)
                .worker("testUser")
                .depositor("testUser2")
                .build();

        Job mockJob = Job.builder()
                .tid(mockTid)
                .money(mockAmount)
                .payment(mockPayHistory)
                .title("ㅋㅋㅋㅋㅋㅋㅋ")
                .build();

        KlayAboutJob mockKlayAboutJob = KlayAboutJob.builder()
                .amtKlay(1.2) // 가상 카이아 값
                .amtKrw(mockAmount)
                .exchangeRate(1.2)
                .job(mockJob)
                .payStatus(PayStatus.PROCESS)
                .build();

        SingleKeyring mockSingleKeyring = mock(SingleKeyring.class);
        when(mockSingleKeyring.getAddress()).thenReturn("tx1231415116t16");
        when(memberService.findById(anyLong())).thenReturn(mockMember);
        when(klayAboutJobRepository.findByJob(any(Job.class))).thenReturn(Optional.of(mockKlayAboutJob));
        when(connectToKlaytnNetwork.getSingleKeyring()).thenReturn(mockSingleKeyring);

        PaySuccessResponseDto result = payService.payTransfer(1L, mockAmount, mockJob);

        assertNotNull(result);
        assertEquals(mockAmount, result.totalPointAmount());
    }

    @Test
    @DisplayName("일 등록 시 payHistory 저장")
    void savePayHistoryByReady() {
        String depositor = "testUser";
        int mockAmount = 1000;
        int mockPoint = 100;

        PayHistory payHistory = payService.savePayHistory(depositor, mockAmount, mockPoint);

        PayHistory findPayHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        assertEquals(payHistory, findPayHistory);
    }

    @Test
    @DisplayName("결제 승인 시 히스토리 상태 업데이트")
    void updatePayHistoryByApprove() {
        String mockTid = "T12351612425";
        int mockAmount = 10000;
        String depositor = "testUser2";
        //payHistory 가상 데이터
        PayHistory mockPayHistory = PayHistory.builder()
                .payStatus(PayStatus.PENDING)
                .payPoint(0)
                .payAmount(mockAmount)
                .worker("")
                .depositor(depositor)
                .build();

        //payHistory 저장
        payHistoryRepository.save(mockPayHistory);

        PayHistory payHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        payService.updatePayHistory(payHistory, PayStatus.PROCESS, "testUser2");

        assertEquals(payHistory.getPayStatus(), PayStatus.PROCESS);
    }

    @Test
    @DisplayName("결제 취소 시 히스토리 상태 업데이트")
    void updatePayHistoryByCancel() {
        String mockTid = "T12351612425";
        int mockAmount = 10000;
        String depositor = "testUser2";
        //payHistory 가상 데이터
        PayHistory mockPayHistory = PayHistory.builder()
                .payStatus(PayStatus.PENDING)
                .payPoint(0)
                .payAmount(mockAmount)
                .worker("")
                .depositor(depositor)
                .build();

        //payHistory 저장
        payHistoryRepository.save(mockPayHistory);

        PayHistory payHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        payService.updatePayHistory(payHistory, PayStatus.FAIL, "testUser2");

        assertEquals(payHistory.getPayStatus(), PayStatus.FAIL);
    }

    @Test
    @DisplayName("일 완료 시 히스토리 상태 업데이트")
    void updatePayHistoryBySuccess() {
        String mockTid = "T12351612425";
        int mockAmount = 10000;
        String depositor = "testUser2";
        //payHistory 가상 데이터
        PayHistory mockPayHistory = PayHistory.builder()
                .payStatus(PayStatus.PROCESS)
                .payPoint(0)
                .payAmount(mockAmount)
                .worker("")
                .depositor(depositor)
                .build();

        //payHistory 저장
        payHistoryRepository.save(mockPayHistory);

        PayHistory payHistory = payHistoryRepository.findByDepositor(depositor).orElseThrow(() -> new GlobalException(ErrorCode.PAY_SUCCESS_NOT_FOUND));

        payService.updatePayHistory(payHistory, PayStatus.SUCCESS, "testUser2");

        assertEquals(payHistory.getPayStatus(), PayStatus.SUCCESS);
    }
}