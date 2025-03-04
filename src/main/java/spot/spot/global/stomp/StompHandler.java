package spot.spot.global.stomp;


import static spot.spot.global.util.ConstantUtil.PERMIT_ALL;
import io.jsonwebtoken.Claims;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.global.redis.service.TokenService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.FilterResponse;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.JwtUtil;
import spot.spot.global.stomp.StompUtil;


@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
	private final StompUtil stompUtil;
	private final JwtUtil jwtUtil;

	@NonNull
	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		String atk = stompUtil.getAccessToken(accessor);
		log.info("STOMP 접근 명령어={}", accessor.getCommand());
		try {
			if(atk == null) throw new MessagingException("JWT TOKEN is NULL");
			switch (accessor.getCommand()) {
				case CONNECT:
					String msgType = accessor.getFirstNativeHeader("type");
					if(Objects.equals(msgType,PERMIT_ALL)) {break;}
				case SUBSCRIBE:
				case SEND:
					stompUtil.verifyAccessToken(atk);
					break;
			}
		} catch (MessagingException e) {
			log.error("STOMP 인증 실패: {}", e.getMessage());
			return stompUtil.handleErrorMessage(accessor, e.getMessage());
		}
		Claims userInfo = jwtUtil.getUserInfoFromToken(atk);
		long memberId = Long.parseLong(userInfo.getSubject());
		Objects.requireNonNull(accessor.getSessionAttributes()).put("memberId", memberId);
		return message;
	}

}
