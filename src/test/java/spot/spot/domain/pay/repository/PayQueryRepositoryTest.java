package spot.spot.domain.pay.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;


@ActiveProfiles("local")
@SpringBootTest
@Transactional
class PayQueryRepositoryTest {

    @Autowired
    PayQueryRepository payQueryRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PayHistoryRepository payHistoryRepository;

    @DisplayName("일 정보로 일의 결제 내역의 결제값을 조회한다.")
    @Test
    void findPayAmountByPayHistory(){
        ///given
        int payAmount = 10000;
        int payPoint = 1000;
        PayHistory payHistory = PayHistory.builder().payStatus(PayStatus.PENDING).payPoint(payPoint).payAmount(payAmount-payPoint).worker("worker").depositor("depositort").build();
        Job job = Job.builder().money(payAmount).title("title").img("img").payment(payHistory).content("content").build();
        payHistoryRepository.save(payHistory);
        jobRepository.save(job);

        ///when
        Integer payAmountByJob = payQueryRepository.findPayAmountByPayHistory(job.getId());

        ///then
        Assertions.assertThat(payAmountByJob).isEqualTo(payAmount - payPoint);
    }
}