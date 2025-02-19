package spot.spot.domain.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import spot.spot.domain.member.entity.jwt.Token;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisTemplate<String, Token> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOKEN_PREFIX = "token:";

    public void saveToken(Token token) {
        String key = TOKEN_PREFIX + token.getAccessToken();
        redisTemplate.opsForValue().set(key, token);
    }

    public Token findToken(String accessToken){
        String key = TOKEN_PREFIX + accessToken;
        Object tokenData = redisTemplate.opsForValue().get(key);

        if(tokenData instanceof LinkedHashMap){
            return objectMapper.convertValue(tokenData, Token.class);
        }
        return (Token) tokenData;
    }

    public void deleteToken(String accessToken) {
        String key = TOKEN_PREFIX + accessToken;
        redisTemplate.delete(key);
    }

    public void updateAccessToken(String accessToken, String newAccessToken) {
        String key = TOKEN_PREFIX + accessToken;
        Token token = redisTemplate.opsForValue().get(key);
        if (token != null) {
            log.info("updateAccessToken = {}", newAccessToken);
            Token updatedToken = Token.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(token.getRefreshToken())
                    .memberId(token.getMemberId())
                    .build();

            redisTemplate.delete(key);
            String newKey = TOKEN_PREFIX + newAccessToken;
            redisTemplate.opsForValue().set(newKey, updatedToken);
        }
    }
}
