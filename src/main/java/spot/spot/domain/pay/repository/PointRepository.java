package spot.spot.domain.pay.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spot.spot.domain.pay.entity.Point;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findFirstByPointCodeAndIsValidTrue(String pointCode);

    List<Point> findByPointCodeAndIsValidTrue(String pointCode);

    void deleteByPointCode(String pointCode);
}
