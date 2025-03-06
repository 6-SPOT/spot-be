package spot.spot.domain.pay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("결제 승인 시 멤버가 누락되면 예외가 발생한다.")
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
                .build();

        when(memberService.findById(anyString())).thenThrow(new GlobalException(ErrorCode.EMPTY_MEMBER));

        // 멤버 누락 exception
        assertThatThrownBy(() -> payService.payApprove("", mockJob, mockPgToken, 10000))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())  // ErrorCode에서 message 가져오기
                .isEqualTo(ErrorCode.EMPTY_MEMBER.getMessage());
    }

    @Test
    @DisplayName("일 완료 시 멤버가 누락되면 예외가 발생한다.")
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
                .build();

        when(memberService.findMemberByIdOrNickname(any(),any())).thenThrow(new GlobalException(ErrorCode.EMPTY_MEMBER));

        assertThatThrownBy(() -> payService.payTransfer("", 10000, mockJob))
                .isInstanceOf(GlobalException.class)
                .extracting(e -> ((GlobalException) e).getErrorCode().getMessage())
                .isEqualTo(ErrorCode.EMPTY_MEMBER.getMessage());
    }
}
