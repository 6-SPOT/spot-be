package spot.spot.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.WorkerAbility;

@Repository
public interface WorkerAbilityRepository extends JpaRepository<WorkerAbility, Long> {

}
