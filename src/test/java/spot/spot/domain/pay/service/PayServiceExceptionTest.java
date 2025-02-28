package spot.spot.domain.pay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
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
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponse;
import spot.spot.domain.pay.repository.KlayAboutJobRepository;
import spot.spot.domain.pay.repository.PayHistoryRepository;
import spot.spot.global.klaytn.api.ExchangeRateByBithumbApi;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class PayServiceExceptionTest {

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
    private MatchingDsl matchingDsl;

    @Autowired
    private PayHistoryRepository payHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Exception 테스트
     * payReady -> memberNickname, job title, amount
     * payApprove -> member, job, pgToken, amount
     * payCancel -> job, amount
     * payOrder -> tid
     * payTransfer -> member, job, amount
     */
    @Test
    @DisplayName("payReady 멤버 누락 예외")
    void payReadyEmptyMemberException() {
        // 멤버 누락 exception
        assertThatThrownBy(() -> payService.payReady(null, "음쓰 버려주실 분~", 10000, 500))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.EMPTY_MEMBER.getMessage());
    }

    @Test
    @DisplayName("payReady 일 정보 누락 예외")
    void payReadyEmptyJobException() {
        // 일 정보 누락 exception
        assertThatThrownBy(() -> payService.payReady("testUser", null, 10000, 500))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.EMPTY_TITLE.getMessage());
    }

    @Test
    @DisplayName("payReady 결제 금액 누락 예외")
    void payReadyEmptyAmountException() {
        // 일 정보 누락 exception
        assertThatThrownBy(() -> payService.payReady("testUser", "음쓰 버려주실 분~", 0, 500))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("payApprove 멤버 누락 예외")
    void payApproveEmptyMemberException() {
        String mockPgToken = "12T12351ASTAH";

        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .id(1L)
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();

        when(memberService.findById(anyString())).thenThrow(new GlobalException(ErrorCode.EMPTY_MEMBER));

        // 멤버 누락 exception
        assertThatThrownBy(() -> payService.payApprove("", mockJob, mockPgToken, 10000))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.EMPTY_MEMBER.getMessage());
    }

    @Test
    @DisplayName("payApprove 일 정보 누락 예외")
    void payApproveEmptyJobException() {
        String mockPgToken = "12T12351ASTAH";

        // Optional이라는 가정
        Job mockJob = Job.builder().build();
        // 일 정보 누락 exception
        assertThatThrownBy(() -> payService.payApprove("1", mockJob, mockPgToken, 10000))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.JOB_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("payApprove pgToken 누락 예외")
    void payApproveEmptyPgTokenException() {
        String mockPgToken = "";

        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .id(1L)
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();
        // 일 정보 누락 exception
        assertThatThrownBy(() -> payService.payApprove("1", mockJob, mockPgToken, 10000))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.EMPTY_PG_TOKEN.getMessage());
    }

    @Test
    @DisplayName("payApprove 가격 누락 예외")
    void payApproveEmptyAmountException() {
        String mockPgToken = "12T12351ASTAH";

        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .id(1L)
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();
        // 일 정보 누락 exception
        assertThatThrownBy(() -> payService.payApprove("1", mockJob, mockPgToken, 0))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("payCancel 일 정보 누락 예외")
    void payCancelEmptyJobException() {
        //Job이 Optional이라고 가정
        Job mockJob = Job.builder().build();

        assertThatThrownBy(() -> payService.payCancel(mockJob, 10000))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.JOB_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("payCancel 가격 누락 예외")
    void payCancelEmptyAmountException() {
        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .id(1L)
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();

        assertThatThrownBy(() -> payService.payCancel(mockJob, 0))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("payOrder tid 누락 예외")
    void PayOrderEmptyTidException() {
        assertThatThrownBy(() -> payService.payOrder(null))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.EMPTY_TID.getMessage());
    }

    @Test
    @DisplayName("payTransfer 멤버 누락 예외")
    void payTransferEmptyMemberException() {

        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .id(1L)
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();

        when(memberService.findById(anyString())).thenThrow(new GlobalException(ErrorCode.EMPTY_MEMBER));

        assertThatThrownBy(() -> payService.payTransfer("", 10000, mockJob))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.EMPTY_MEMBER.getMessage());
    }

    @Test
    @DisplayName("payTransfer 일 정보 누락")
    void payTransferEmptyJobException() {
        //Optional이라는 가정
        Job mockJob = Job.builder().build();

        assertThatThrownBy(() -> payService.payTransfer("1", 10000, mockJob))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.JOB_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("payTransfer 가격 누락")
    void payTransferEmptyAmountException() {
        PayHistory mockPayHistory = PayHistory.builder().payAmount(10000)
                .payPoint(500)
                .depositor("testUser1")
                .worker("")
                .payStatus(PayStatus.PENDING)
                .build();

        Job mockJob = Job.builder().title("음쓰 버려주실 분~")
                .id(1L)
                .money(10000)
                .tid("T1234ABCD5678")
                .payment(mockPayHistory)
                .build();

        assertThatThrownBy(() -> payService.payTransfer("1", 0, mockJob))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.INVALID_AMOUNT.getMessage());
    }
}
