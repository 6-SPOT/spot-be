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
 import org.springframework.transaction.annotation.Transactional;
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
 import java.util.Optional;
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

     @ParameterizedTest
     @ValueSource(ints = {0, 1})
     @DisplayName("포인트 등록 시 입력한 포인트코드와 일치하는 포인트를 찾아 사용했음으로 변경한다.")
     @Transactional
     void registerPoint(int number) {
         Member findMember = memberService.findByNickname("테스트유저1");
         String pointCode = responseDtos.get(number).pointCode();
         Point point = pointRepository.findByPointCode(pointCode).get();
         int beforeCount = point.getCount();
         pointService.registerPoint(pointCode, String.valueOf(findMember.getId()));
         Point afterPoint = pointRepository.findByPointCode(pointCode).get();
         int afterCount = afterPoint.getCount();

         assertThat(beforeCount).isEqualTo(afterCount + 1);
     }

     @ParameterizedTest
     @ValueSource(ints = {0, 1})
     @DisplayName("포인트 생성 시 포인트의 갯수만큼 사용할 수 있는 포인트가 저장된다.")
     @Transactional
     void servePoint(int number) {
         String pointCode = responseDtos.get(number).pointCode();
         Point point = pointRepository.findByPointCode(pointCode).get();

         assertThat(point.getCount()).isEqualTo(responseDtos.get(number).count());
     }

     @ParameterizedTest
     @ValueSource(ints = {0, 1})
     @DisplayName("포인트 등록이 성공하면 유저의 포인트가 증가한다.")
     @Transactional
     void registerPointWithMemberPoint(int number) {
         ///given
         Member findMember = memberService.findByNickname("테스트유저1");
         String pointCode = responseDtos.get(number).pointCode();
         Point point = pointRepository.findByPointCode(pointCode).get();

         ///when
         pointService.registerPoint(pointCode, String.valueOf(findMember.getId()));

         ///then
         assertThat(memberService.findByNickname("테스트유저1").getPoint()).isEqualTo(point.getPoint());
     }

     @ParameterizedTest
     @ValueSource(ints = {0, 1})
     @DisplayName("포인트코드가 일치하는 모든 포인트를 삭제한다.")
     @Transactional
     void deletePoint(int number) {
         String pointCode = responseDtos.get(number).pointCode();
         pointService.deletePoint(pointCode);

         ///when
         Optional<Point> findPoint = pointRepository.findByPointCode(pointCode);

         assertThat(findPoint).isEmpty();
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
                     log.info("{} 번째 쓰레드 성공", finalI);
                 } catch (Exception e) {
                     log.info("{} 번째 쓰레드 실패", finalI);
                     failCount.getAndIncrement();
                 } finally {
                     log.info("{} 번째 쓰레드 접근 종료", finalI);
                     doneSignal.countDown();
                 }
             });
         }

         /// 스레드 대기 종료
         doneSignal.await();
         executorService.shutdown();

         /// then
         int resultFailThreads = threads - 5;
         assertThat(resultFailThreads).isEqualTo(failCount.get());
     }
 }
