package spot.spot.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.spot.domain.member.entity.Ability;

public interface AbilityRepository extends JpaRepository<Ability, Long> {

}
