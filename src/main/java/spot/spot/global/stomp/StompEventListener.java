package spot.spot.global.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

	private final ActiveSessionTracker sessionTracker;

	@EventListener
	public void connectionHandle(SessionConnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
		log.info("연결 성공 SESSION ID: {}", sessionId);
		sessionTracker.addSession(sessionId);
	}

	@EventListener
	public void disconnectionHandle(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();

		if(sessionTracker.isSessionActive(sessionId)){
			log.warn("비정상 종료 감지!, 세션 ID: {}", sessionId);
		}
		sessionTracker.removeSession(sessionId);
	}
}
