package spot.spot.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.job.entity.Matching;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

}
