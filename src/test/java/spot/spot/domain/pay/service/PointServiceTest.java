package spot.spot.domain.pay.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.pay.entity.Point;
import spot.spot.domain.pay.entity.dto.PointServeDto;
import spot.spot.domain.pay.repository.PointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Slf4j
class PointServiceTest {

    @Autowired
    PointService pointService;

    @Autowired
    PointRepository pointRepository;

    @BeforeEach
    void before() {
        PointServeDto pointServeDto = new PointServeDto("포인트1", 1000, "AAAAA");
        List<PointServeDto> serveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            serveList.add(pointServeDto);
        }

        for (PointServeDto dto : serveList) {
            pointService.servePoint(dto.pointName(), dto.point(), dto.pointCode());
        }
    }

    @Test
    void servePoint() {
        Optional<Point> findPointCoupon = pointRepository.findFirstByPointCodeAndIsValidTrue("AAAAA");

        log.info("findPointCoupon ={}", findPointCoupon.get().getPointName());

        Assertions.assertThat(findPointCoupon.get().getPointName()).isEqualTo("포인트1");
        Assertions.assertThat(findPointCoupon.get().getPoint()).isEqualTo(1000);
    }

    @Test
    void registerPoint() {
        pointService.registerPoint("AAAAA", "1");

        List<Point> findPointCoupons = pointRepository.findByPointCodeAndIsValidTrue("AAAAA");

        Assertions.assertThat(findPointCoupons.size()).isEqualTo(4);
    }

    @Test
    void deletePoint() {
        pointService.deletePoint("AAAAA");

        List<Point> findPointCoupon = pointRepository.findByPointCodeAndIsValidTrue("AAAAA");

        Assertions.assertThat(findPointCoupon.size()).isEqualTo(0);
    }
}