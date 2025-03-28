package spot.spot.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.Ability;
import spot.spot.domain.member.entity.AbilityType;

@Repository
public interface AbilityRepository extends JpaRepository<Ability, Long> {

    Optional<Ability> findByType(AbilityType type);
}
