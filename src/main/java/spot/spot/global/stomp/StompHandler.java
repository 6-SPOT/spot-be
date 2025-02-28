package spot.spot.global.stomp;

import static spot.spot.global.util.ConstantUtil.*;

import java.util.Objects;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.global.redis.service.TokenService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.FilterResponse;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.JwtUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

	private final JwtUtil jwtUtil;
	private final FilterResponse filterResponse;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

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
