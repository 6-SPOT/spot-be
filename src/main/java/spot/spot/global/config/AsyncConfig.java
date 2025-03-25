package spot.spot.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50); // 기본 pool size
        executor.setMaxPoolSize(200); // 최대 pool size
        executor.setQueueCapacity(10000); // Thread를 이미 10개 사용하고 있을 때, 작업을 대기하는 큐의 크기 (100개의 작업 저장 - 그 이상 거부됨)
        executor.setThreadNamePrefix("Async-Executor-");
        executor.initialize();
        return new DelegatingSecurityContextExecutor(executor);
        /*
        * Security 붙이면 위의 걸로 바꿔야 함. -> 안 바꾸면, 각 Thread마다 현 접속 유저 유지가 안됨.
        * */
    }
}
