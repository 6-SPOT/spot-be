package spot.spot.domain.member.entity.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
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
