 package spot.spot.domain.pay.service;

 import lombok.extern.slf4j.Slf4j;
 import org.junit.jupiter.api.AfterEach;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.params.ParameterizedTest;
 import org.junit.jupiter.params.provider.ValueSource;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.test.context.ActiveProfiles;
 import spot.spot.domain.member.dto.request.MemberRequest;
 import spot.spot.domain.member.entity.Member;
 import spot.spot.domain.member.repository.MemberRepository;
 import spot.spot.domain.member.service.MemberService;
 import spot.spot.domain.pay.entity.Point;
 import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
 import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
 import spot.spot.domain.pay.repository.PointRepository;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.atomic.AtomicInteger;

 import static org.assertj.core.api.Assertions.assertThat;

 @SpringBootTest
 @ActiveProfiles("local")
 @Slf4j
 class PointServiceTest {

     @Autowired
     PointService pointService;

     @Autowired
     PointRepository pointRepository;

     @Autowired
     MemberService memberService;

     @Autowired
     MemberRepository memberRepository;

     List<PointServeResponseDto> responseDtos = new ArrayList<>();

     @BeforeEach
     void before() {
         pointRepository.deleteAllInBatch();
         memberRepository.deleteAllInBatch();
         MemberRequest.register build = MemberRequest.register.builder()
                 .nickname("테스트유저1")
                 .email("test@test.com")
                 .img("img")
                 .build();
         memberService.register(build);
         PointServeRequestDto serveCountPoint = new PointServeRequestDto("포인트1", 1000, 3);
         PointServeRequestDto serveCountPoint2 = new PointServeRequestDto("포인트2", 1000, 5);

         List<PointServeRequestDto> serveRequestDtos = new ArrayList<>();

         serveRequestDtos.add(serveCountPoint);
         serveRequestDtos.add(serveCountPoint2);

         responseDtos = pointService.servePoint(serveRequestDtos);
     }

     @AfterEach
     void after() {
         memberRepository.deleteAllInBatch();
         pointRepository.deleteAllInBatch();
     }

     @Test
     @DisplayName("포인트 생성 시 포인트의 갯수만큼 사용할 수 있는 포인트가 저장된다.")
     void servePoint() {
         for (int i = 0; i < responseDtos.size(); i++) {
             String pointCode = responseDtos.get(i).pointCode();
             List<Point> byPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);

             Point point = byPointCodeAndIsValidTrue.get(0);
         }

         List<Point> all = pointRepository.findAll();
         assertThat(all.size()).isEqualTo(8);
     }

     @Test
     @DisplayName("포인트 등록 시 입력한 포인트코드와 일치하는 포인트를 찾아 사용했음으로 변경한다.")
     void registerPoint() {
         Member findMember = memberService.findByNickname("테스트유저1");
         log.info("memberId= {}", findMember.getId());
         for (int i = 0; i < responseDtos.size(); i++) {
            String pointCode = responseDtos.get(i).pointCode();
            List<Point> byPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
            int beforePointCount = byPointCodeAndIsValidTrue.size();
            pointService.registerPoint(pointCode, String.valueOf(findMember.getId()));
             List<Point> afterByPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int afterRegisterPoint = afterByPointCodeAndIsValidTrue.size();

             assertThat(beforePointCount).isEqualTo(afterRegisterPoint + 1);
         }
     }

     @Test
     @DisplayName("포인트 등록이 성공하면 유저의 포인트가 증가한다.")
     void registerPointWithMemberPoint() {
         ///given
         Member findMember = memberService.findByNickname("테스트유저1");

         ///when
         for (int i = 0; i < responseDtos.size(); i++) {
             String pointCode = responseDtos.get(i).pointCode();
             pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             pointService.registerPoint(pointCode, String.valueOf(findMember.getId()));
         }

         ///then
         assertThat(memberService.findByNickname("테스트유저1").getPoint()).isEqualTo(2000);
     }

     @Test
     @DisplayName("포인트코드가 일치하는 포인트를 한개 삭제한다.")
     void deletePointOnce() {
         for (int i = 0; i < responseDtos.size(); i++) {
             String pointCode = responseDtos.get(i).pointCode();
             List<Point> byPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int beforePointCount = byPointCodeAndIsValidTrue.size();
             pointService.deletePointOnce(pointCode);
             List<Point> afterByPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int afterRegisterPoint = afterByPointCodeAndIsValidTrue.size();

             assertThat(beforePointCount).isEqualTo(afterRegisterPoint + 1);
         }

         List<Point> all = pointRepository.findAll();
         assertThat(all.size()).isEqualTo(6);
     }

     @Test
     @DisplayName("포인트코드가 일치하는 모든 포인트를 삭제한다.")
     void deletePoint() {
         for (int i = 0; i < responseDtos.size(); i++) {
             String pointCode = responseDtos.get(i).pointCode();
             pointService.deletePoint(pointCode);
             List<Point> afterByPointCodeAndIsValidTrue = pointRepository.findByPointCodeAndIsValidTrue(pointCode);
             int afterRegisterPoint = afterByPointCodeAndIsValidTrue.size();

             assertThat(afterRegisterPoint).isEqualTo(0);
         }

         List<Point> all = pointRepository.findAll();

         assertThat(all.size()).isEqualTo(0);
     }

     @DisplayName("포인트가 개수가 5개인 포인트를 동시에 5, 10, 15 개의 쓰레드가 등록하려고 하면 0개, 5개, 10 개의 쓰레드가 실패한다.")
     @ParameterizedTest
     @ValueSource(ints = {5, 10, 15})
     void registerPointMultiThread(int threads) throws InterruptedException {
         ///given
         Member findMember = memberService.findByNickname("테스트유저1");
         CountDownLatch doneSignal = new CountDownLatch(threads);
         ExecutorService executorService = Executors.newFixedThreadPool(threads);

         AtomicInteger successCount = new AtomicInteger(0);
         AtomicInteger failCount = new AtomicInteger(0);
         String pointCode = responseDtos.get(1).pointCode();

         /// when
         for (int i = 0; i < threads; i++) {
             int finalI = i;
             executorService.execute(() -> {
                 try {
                     log.info("{} 번째 쓰레드 접근 시작", finalI);
                     pointService.registerPoint(pointCode, String.valueOf(findMember.getId()));
                     successCount.getAndIncrement();
                 } catch (Exception e) {
                     failCount.getAndIncrement();
                 } finally {
                     log.info("{} 번째 쓰레드 접근 종료", finalI);
                     doneSignal.countDown();
                 }
             });
         }

         /// 스레드 종료 대기
         doneSignal.await();
         executorService.shutdown();

         /// then
         int resultFailThreads = threads - 5;
         assertThat(resultFailThreads).isEqualTo(failCount.get());
     }
 }
