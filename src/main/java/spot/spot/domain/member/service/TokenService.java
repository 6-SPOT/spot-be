package spot.spot.domain.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import spot.spot.global.security.util.jwt.Token;

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
        String refreshTokenKey = TOKEN_PREFIX + token.getRefreshToken();
        redisTemplate.opsForValue().set(key, token);
        redisTemplate.opsForValue().set(refreshTokenKey, token);
    }

    public Token findToken(String accessToken){
        String key = TOKEN_PREFIX + accessToken;
        Object tokenData = redisTemplate.opsForValue().get(key);

        if(tokenData instanceof LinkedHashMap){
            return objectMapper.convertValue(tokenData, Token.class);
        }
        return (Token) tokenData;
    }

    public Token findByRefreshToken(String refreshToken) {
        String key = TOKEN_PREFIX + refreshToken;
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

    public void updateAccessToken(String accessToken,String refreshToken, String newAccessToken) {
        String key = TOKEN_PREFIX + accessToken;
        String refreshKey = TOKEN_PREFIX + refreshToken;
        Token token = redisTemplate.opsForValue().get(key);
        if (token != null) {
            log.info("updateAccessToken = {}", newAccessToken);
            Token updatedToken = Token.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(token.getRefreshToken())
                    .memberId(token.getMemberId())
                    .build();

            redisTemplate.delete(key);
            redisTemplate.delete(refreshKey);
            String newKey = TOKEN_PREFIX + newAccessToken;
            String newRefreshKey = TOKEN_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(newKey, updatedToken);
            redisTemplate.opsForValue().set(newRefreshKey, updatedToken);
        }
    }
}
