package spot.spot.global.redis.service;

import static spot.spot.global.util.ConstantUtil.TOKEN_PREFIX;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import spot.spot.global.redis.entity.Token;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisTemplate<String, Token> redisTemplate;

    public void saveToken(Token token) {
        String key = TOKEN_PREFIX + token.getAccessToken();
        String refreshTokenKey = TOKEN_PREFIX + token.getRefreshToken();
        redisTemplate.opsForValue().set(key, token);
        redisTemplate.opsForValue().set(refreshTokenKey, token);
    }

    public Token findToken(String accessToken){
        String key = TOKEN_PREFIX + accessToken;
        return redisTemplate.opsForValue().get(key);
    }

    public Token findByRefreshToken(String refreshToken) {
        String key = TOKEN_PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteToken(String accessToken) {
        String key = TOKEN_PREFIX + accessToken;
        redisTemplate.delete(key);
    }

    public void updateAccessToken(String accessToken,String refreshToken, String newAccessToken) {
        String key = TOKEN_PREFIX + accessToken;
        String refreshKey = TOKEN_PREFIX + refreshToken;
        Token token = redisTemplate.opsForValue().get(key);
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
