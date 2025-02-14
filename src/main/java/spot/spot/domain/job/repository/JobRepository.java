package spot.spot.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.job.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

}
