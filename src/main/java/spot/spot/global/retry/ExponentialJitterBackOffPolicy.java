package spot.spot.global.retry;

import com.google.api.client.util.ExponentialBackOff;
import java.util.Random;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

@Slf4j
public class ExponentialJitterBackOffPolicy implements BackOffPolicy {

    // BACK-OFF POLICY: 전략 클래스 - 어떻게 재시도 할지 전략만 정의
    // BACK-OFF CONTEXT: 상태 클래스 -> 개별 재시도 루틴마다의 상태를 기록 후 저장

    private final Random random = new Random();

    @Override
    public BackOffContext start(RetryContext retryContext) {
        return new ExponentialJitterBackOffContext(1000L, 2.0, 5000L);
    }

    @Override
    public void backOff(BackOffContext backOffContext) {
        if(backOffContext instanceof  ExponentialJitterBackOffContext ctx) {
            long jitter = (long) (ctx.currentInterval * 0.8 * random.nextDouble());
            long delay  = ctx.currentInterval - jitter / 2 + jitter;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // 현재 스레드 인터럽트 상태 복구 (권장 패턴)
                Thread.currentThread().interrupt();
                // 로그 남기기
                log.warn("다른 쓰레드가 잠든 쓰레드를 깨워버렸습니다. {}", e.getMessage());
            }

            long next = (long) (ctx.currentInterval * ctx.multiplier);
            ctx.currentInterval = Math.min(next, ctx.maxInterval);
        }
    }

    private static class ExponentialJitterBackOffContext implements BackOffContext {
        private long currentInterval;
        private final double multiplier;
        private final long maxInterval;

        public ExponentialJitterBackOffContext(long initialInterval, double multiplier, long maxInterval) {
            this.currentInterval = initialInterval;
            this.multiplier = multiplier;
            this.maxInterval = maxInterval;
        }
    }


}
