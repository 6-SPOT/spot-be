package spot.spot.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.HashTag;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {

}
