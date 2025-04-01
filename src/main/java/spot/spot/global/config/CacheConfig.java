package spot.spot.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("job-geohash");
        manager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(200_000)
            .expireAfterWrite(Duration.ofDays(1))
            .recordStats());
        return manager;
    }
}
