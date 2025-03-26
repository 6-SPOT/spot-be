package spot.spot.global.retry;


import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3,
            Map.of(
                RejectedExecutionException.class, true
            ));

        ExponentialJitterBackOffPolicy jitterPolicy = new ExponentialJitterBackOffPolicy();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(jitterPolicy);

        return template;
    }
}
