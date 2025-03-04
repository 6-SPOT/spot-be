package spot.spot.global.stomp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/api/connect")
			.setAllowedOriginPatterns("*")
			.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 메세지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
		registry.setApplicationDestinationPrefixes("/api/publish");
		// 메세지를 수신해야 함을 설정
		registry.enableSimpleBroker("/api/topic");
        registry.setUserDestinationPrefix("/user");	// 1대 1 메시지 전용
    }

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}

	// STOMP 에서 64KB 이상의 데이터 전송하기 위한 설정
	// 기본값: 64KB, 전송 제한 시간: 10초, bufferSize: 512KB
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(160 * 64 * 1024);
		registry.setSendTimeLimit(100 * 10000);
		registry.setSendBufferSizeLimit(3 * 512 * 1024);
	}
}
