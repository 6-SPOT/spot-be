package spot.spot.global.security.util.jwt;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@RedisHash(value = "userToken", timeToLive = 86400)
public class Token {

    @Id
    private String refreshToken;

    @Indexed
    @Setter
    private String accessToken;

    @Indexed
    private String memberId;
}
