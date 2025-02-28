package spot.spot.global.stomp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class StompEventListener {

	@EventListener
	public void connectionHandle(SessionConnectEvent event) {
		// 연결 된 경우
	}

	@EventListener
	public void disconnectionHandle(SessionDisconnectEvent event) {
		// 연결을 끊는 경우
	}
}
