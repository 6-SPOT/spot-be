package spot.spot.domain.pay.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import spot.spot.domain.pay.entity.Point;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Point> findFirstByPointCodeAndIsValidTrue(String pointCode);

    List<Point> findByPointCodeAndIsValidTrue(String pointCode);

    void deleteByPointCode(String pointCode);

    Optional<Point> findFirstByPointCode(String pointCode);
}
