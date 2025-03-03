package spot.spot.global.stomp;
import io.jsonwebtoken.Claims;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.FilterResponse;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

	private final JwtUtil jwtUtil;
	private final FilterResponse filterResponse;

    @NonNull
	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("STOMP 접근 명령어={}", accessor.getCommand());

        if(StompCommand.CONNECT == accessor.getCommand()) {
			String authHeader = accessor.getFirstNativeHeader("Authorization");
			if(Objects.isNull(authHeader)) {
				throw new GlobalException(ErrorCode.NOT_FOUND_JWT);
			}
			String token = jwtUtil.separateBearer(authHeader);
			ErrorCode error  = jwtUtil.validateToken(token);
			if(error != null) {
				throw new GlobalException(ErrorCode.INVALID_JWT);
			}
			Claims userInfo = jwtUtil.getUserInfoFromToken(token);
			long memberId = Long.parseLong(userInfo.getSubject());
			Objects.requireNonNull(accessor.getSessionAttributes()).put("memberId", memberId);
		}
		return message;
	}
}
