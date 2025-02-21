package spot.spot.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

}
