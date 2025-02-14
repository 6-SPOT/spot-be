package spot.spot.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-local.yml")
public class RedisConfigTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void redisConnctionTest() {
        // 1. redis에 데이터 넣기
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("testKey", "Hello Redis");
        // 2. 저장한 데이터 가져오기
        String value = valueOperations.get("testKey");
        // 3. 값이 정상적인지 검증
        assertThat(value).isEqualTo("Hello Redis");
    }
}
