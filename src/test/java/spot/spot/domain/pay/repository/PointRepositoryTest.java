package spot.spot.domain.pay.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.pay.entity.Point;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@Transactional
@ActiveProfiles("local")
class PointRepositoryTest {

    @Autowired
    PointRepository pointRepository;

    @DisplayName("사용하지 않은 포인트를 일치하는 포인트코드로 조회하면 일치하는 포인트 중 하나가 조회된다.")
    @Test
    void findFirstByPointCodeAndIsValidTrue(){
        ///given
        String pointCode = "22444WX";
        Point point1 = Point.builder().pointName("point1").point(1000).pointCode(pointCode).isValid(true).build();
        pointRepository.save(point1);

        ///when
        Optional<Point> findPoint = pointRepository.findFirstByPointCodeAndIsValidTrue(pointCode);

        ///then
        Assertions.assertThat(findPoint.get())
                .extracting("pointName", "pointCode", "point")
                .containsExactly("point1", pointCode, 1000);
    }

    @DisplayName("사용하지 않은 포인트를 일치하는 포인트 코드로 조회하면 일치하는 모든 포인트가 조회된다.")
    @Test
    void findByPointCodeAndIsValidTrue(){
        ///given
        String pointCode = "22444WX";
        for (int i = 0; i < 5; i++) {
            boolean status = false;
            if(i % 2 == 0) status = true;
            Point point1 = Point.builder().pointName("point1").point(1000).pointCode(pointCode).isValid(status).build();
            pointRepository.save(point1);
        }

        ///when
        List<Point> pointList = pointRepository.findByPointCodeAndIsValidTrue(pointCode);

        ///then
        assertThat(pointList.size()).isEqualTo(3);
    }

    @DisplayName("포인트코드로 일치하는 포인트를 전체 삭제한다.")
    @Test
    void deleteByPointCode(){
        ///given
        String pointCode = "22444WX";
        for (int i = 0; i < 5; i++) {
            Point point1 = Point.builder().pointName("point1").point(1000).pointCode(pointCode).isValid(true).build();
            pointRepository.save(point1);
        }

        ///when
        pointRepository.deleteByPointCode(pointCode);

        ///then
        assertThat(pointRepository.findFirstByPointCode(pointCode)).isEmpty();
    }

    @DisplayName("포인트코드로 일치하는 포인트를 하나 조회한다.")
    @Test
    void findFirstByPointCode(){
        ///given
        String pointCode = "22444WX";
        for (int i = 0; i < 5; i++) {
            Point point1 = Point.builder().pointName("point1").point(1000).pointCode(pointCode).isValid(true).build();
            pointRepository.save(point1);
        }

        ///when
        Optional<Point> firstByPointCode = pointRepository.findFirstByPointCode(pointCode);

        ///then
        assertThat(firstByPointCode.get()).isNotNull();
    }
}