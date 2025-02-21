package spot.spot.global.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.logging.ColorLogger;

@Configuration
public class TestDataInitializerConfig {

    // CommandLineRunner 클래스: Spring Boot 실행 시 한 번만 실행됨 -> DB 초기 데이터 삽입, 설정값 로드 캐시 초기화 작업에 쓰임.
    @Bean
    public CommandLineRunner initDatabase(MemberRepository memberRepository) {
        return args -> {
            Long testId = 1L; // ID -1을 기준으로 확인
            boolean exists = memberRepository.existsById(testId);

            if (!exists) {
                // 테스트 멤버 생성 (ID는 자동 할당됨)
                Member testMember = Member.builder()
                    .email("test@example.com")
                    .nickname("테스트유저")
                    .lat(37.5665)
                    .lng(126.9780)
                    .point(100)
                    .build();

                // 멤버 저장 후, 실제 저장된 ID 확인
                Member savedMember = memberRepository.save(testMember);
                ColorLogger.green("테스트 멤버 추가 완료: ID = " + savedMember.getId());
            } else {
                ColorLogger.red("테스트 멤버가 이미 존재함: ID = " + testId);
            }
        };
    }
}
