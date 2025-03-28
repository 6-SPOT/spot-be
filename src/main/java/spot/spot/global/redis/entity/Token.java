package spot.spot.global.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@RedisHash(value = "token:", timeToLive = 86400)
public class Token {

    @Id
    private String refreshToken;

    private String accessToken;
    private String memberId;
}
