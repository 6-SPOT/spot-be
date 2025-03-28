package spot.spot.domain.pay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    JPAQueryFactory jpaQueryFactory;

    @DisplayName("포인트코드가 일치하는 포인트 코드를 조회할 수 있다.")
    @Test
    void findFirstByPointCodeAndIsValidTrue(){
        ///given
        String pointCode = "22444WX";
        Point point1 = Point.builder().pointName("point1").point(1000).count(10).pointCode(pointCode).build();
        pointRepository.save(point1);

        ///when
        Optional<Point> findPoint = pointRepository.findByPointCode(pointCode);

        ///then
        Assertions.assertThat(findPoint.get())
                .extracting("pointName", "pointCode", "point")
                .containsExactly("point1", pointCode, 1000);
    }

    @DisplayName("포인트코드로 일치하는 포인트를 전체 삭제한다.")
    @Test
    void deleteByPointCode(){
        ///given
        String pointCode = "22444WX";
        for (int i = 0; i < 5; i++) {
            Point point1 = Point.builder().pointName("point1").point(1000).pointCode(pointCode).count(0).build();
            pointRepository.save(point1);
        }

        ///when
        pointRepository.deleteByPointCode(pointCode);

        ///then
        assertThat(pointRepository.findByPointCode(pointCode)).isEmpty();
    }

}