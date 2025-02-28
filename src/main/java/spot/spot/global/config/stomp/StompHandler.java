package spot.spot.global.config.stomp;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.member.service.TokenService;
import spot.spot.global.security.util.jwt.JwtUtil;
import spot.spot.global.security.util.jwt.Token;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

	private final JwtUtil jwtUtil;
	private final TokenService tokenService;
	private final MemberService memberService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if(StompCommand.CONNECT == accessor.getCommand()) {
			String token = accessor.getFirstNativeHeader("Authorization");

			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7); // "Bearer " 제거

				if (!jwtUtil.isExpired(token)) {
					authenticateToken(token, accessor);  // JWT 검증 및 인증 처리
				} else {
					handleExpiredToken(token, message);
				}
			}
		}
		return message;
	}


	private void authenticateToken(String token, StompHeaderAccessor accessor) {
		if (!jwtUtil.isExpired(token)) {
			Authentication authentication = jwtUtil.getAuthentication(token);
			Long memberId = Long.parseLong(authentication.getName());
			Objects.requireNonNull(accessor.getSessionAttributes()).put("memberId", memberId);
		}
	}

	private void handleExpiredToken(String token, Message<?> message) {
		Token redisToken = tokenService.findToken(token);
		String loginId = jwtUtil.getLoginId(redisToken.getRefreshToken());

		if (redisToken != null && jwtUtil.isExpired(redisToken.getRefreshToken())) {
			log.info("RefreshToken 만료");
			// RefreshToken도 만료된 경우, 연결 차단 (인증 실패)
			throw new AccessDeniedException("RefreshToken 만료");
		} else if (redisToken != null) {
			// RefreshToken이 유효한 경우 AccessToken 재발급
			String newAccessToken = jwtUtil.getAccessToken((OAuth2Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			redisToken.setAccessToken(newAccessToken);
			tokenService.saveToken(redisToken);
			// WebSocket 연결 헤더에 새 AccessToken을 추가하거나, 재인증 로직 수행
			SecurityContextHolder.getContext().setAuthentication(jwtUtil.getAuthentication(newAccessToken));
		} else {
			tokenService.deleteToken(loginId);
		}
	}
}
