 package spot.spot.domain.pay.service;

 import lombok.extern.slf4j.Slf4j;
 import org.assertj.core.api.Assertions;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.transaction.annotation.Transactional;
 import spot.spot.domain.pay.entity.Point;
 import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
 import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
 import spot.spot.domain.pay.repository.PointRepository;

 import java.util.ArrayList;
 import java.util.List;

 @SpringBootTest
 @Transactional
 @Slf4j
 class PointServiceTest {

     @Autowired
     PointService pointService;

     @Autowired
     PointRepository pointRepository;

     List<PointServeResponseDto> responseDtos = new ArrayList<>();

     @BeforeEach
     void before() {
         PointServeRequestDto serveCountPoint = new PointServeRequestDto("포인트1", 1000, 3);
         PointServeRequestDto serveCountPoint2 = new PointServeRequestDto("포인트2", 1000, 5);

         List<PointServeRequestDto> serveRequestDtos = new ArrayList<>();

         serveRequestDtos.add(serveCountPoint);
         serveRequestDtos.add(serveCountPoint2);

         responseDtos = pointService.servePoint(serveRequestDtos);
     }

     @Test
     void servePoint() {
         for (int i = 0; i < responseDtos.size(); i++) {
             String pointCode = responseDtos.get(i).pointCode();
             log.info("pointCode = {}", pointCode);
             List<Point> byPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);

             Point point = byPointCodeAndIsValidTrue.get(0);
             log.info("name = {}, point = {}, pointCode = {}", point.getPointName(), point.getPoint(), point.getPointCode());
         }

         List<Point> all = pointRepository.findAll();
         Assertions.assertThat(all.size()).isEqualTo(8);
     }

     @Test
     void registerPoint() {
         for (int i = 0; i < responseDtos.size(); i++) {
            String pointCode = responseDtos.get(i).pointCode();
            List<Point> byPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
            int beforePointCount = byPointCodeAndIsValidTrue.size();
            pointService.registerPoint(pointCode, "1");
             List<Point> afterByPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int afterRegisterPoint = afterByPointCodeAndIsValidTrue.size();

             Assertions.assertThat(beforePointCount).isEqualTo(afterRegisterPoint + 1);
         }
     }

     @Test
     void deletePoint() {
         for (int i = 0; i < responseDtos.size(); i++) {
             String pointCode = responseDtos.get(i).pointCode();
             List<Point> byPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int beforePointCount = byPointCodeAndIsValidTrue.size();
             pointService.deletePoint(pointCode);
             List<Point> afterByPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int afterRegisterPoint = afterByPointCodeAndIsValidTrue.size();

             log.info("point Size before = {}, point Size after ={}", beforePointCount, afterRegisterPoint);
             Assertions.assertThat(beforePointCount).isEqualTo(afterRegisterPoint + 1);
         }

         List<Point> all = pointRepository.findAll();

         Assertions.assertThat(all.size()).isEqualTo(6);
     }
 }
