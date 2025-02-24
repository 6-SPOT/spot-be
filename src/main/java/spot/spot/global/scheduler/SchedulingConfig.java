package spot.spot.global.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulingConfig { // 비동기 스케줄러는 비동기 쓰레드와 다르게 풀 사이즈가 고정이고 기본적으로 큐가 없다. -> 커스텀 하면 가능
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // 동시에 실행할 스레드 수 설정
        scheduler.setThreadNamePrefix("scheduled-task-"); //스레드 이름 접두사
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true); //작업 완료까지 스프링종료를 대기
        scheduler.initialize();
        return scheduler;
    }
}
