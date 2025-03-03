package spot.spot.global.stomp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompEventListener {

	@EventListener
	public void connectionHandle(SessionConnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
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
