package spot.spot.domain.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.pay.entity.PayHistory;

import java.util.Optional;

public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {

    Optional<PayHistory> findByDepositor(String depositor);

    Optional<PayHistory> findByJobAndDepositor(Job job, String depositor);

    Optional<PayHistory> findByJobAndWorker(Job job, String worker);
}
