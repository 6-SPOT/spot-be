package spot.spot.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.job.entity.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

}
