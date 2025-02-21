package spot.spot.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import spot.spot.global.security.util.jwt.Token;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories(basePackages = {"spot.spot.domain.job.repository", "spot.spot.domain.member.repository"})
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("#{new Integer('${spring.data.redis.port}')}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;

    // JPA 처럼 사용하기 -> Entity 형태 지원
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    // redisTemplate 형태 지원
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Token> redisTemplateToken(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Token> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // ✅ LocalDateTime 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ✅ 날짜를 ISO 형식으로 저장

        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Token.class);

        // ✅ Redis Key는 String, Value는 JSON
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}
