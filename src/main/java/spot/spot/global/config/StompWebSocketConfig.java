package spot.spot.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

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
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		// 웹소켓 요청시 검증 로직 작성 가능
		WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
	}
}
