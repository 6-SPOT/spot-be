package spot.spot.domain.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.pay.entity.KlayAboutJob;

import java.util.Optional;

public interface KlayAboutJobRepository extends JpaRepository<KlayAboutJob, Long> {
    Optional<KlayAboutJob> findByJob(Job job);
}
