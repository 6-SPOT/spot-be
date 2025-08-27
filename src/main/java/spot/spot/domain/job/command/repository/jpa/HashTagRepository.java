package spot.spot.domain.job.command.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.HashTag;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {

}
