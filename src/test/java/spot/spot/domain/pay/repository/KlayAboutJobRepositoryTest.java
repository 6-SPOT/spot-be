package spot.spot.domain.pay.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.pay.entity.KlayAboutJob;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.PayStatus;

import java.util.Optional;

@ActiveProfiles("local")
@SpringBootTest
@Transactional
class KlayAboutJobRepositoryTest {

    @Autowired
    KlayAboutJobRepository klayAboutJobRepository;

    @Autowired
    PayHistoryRepository payHistoryRepository;

    @Autowired
    JobRepository jobRepository;

    @DisplayName("일 정보로 클레이환율정보를 가져올 수 있다.")
    @Test
    void findByJob(){
        ///given
        double amtKlay = 0.00000001;
        int amtKrw = 10000;
        Job job = Job.builder().money(10000).title("title").img("img").content("content").build();
        Job saveJob = jobRepository.save(job);
        PayHistory payHistory = PayHistory.builder().job(saveJob).payStatus(PayStatus.PENDING).payPoint(1000).payAmount(9000).worker("worker").depositor("depositort").build();
        KlayAboutJob klayAboutjob = KlayAboutJob.builder().job(saveJob).amtKlay(amtKlay).amtKrw(amtKrw).exchangeRate(0.0001).build();
        payHistoryRepository.save(payHistory);
        klayAboutJobRepository.save(klayAboutjob);
        ///when
        Optional<KlayAboutJob> findKlay = klayAboutJobRepository.findByJob(job);

        ///then
        Assertions.assertThat(findKlay.get()).isNotNull()
                .extracting("amtKrw", "amtKlay")
                .containsExactly(amtKrw, amtKlay);
    }
}