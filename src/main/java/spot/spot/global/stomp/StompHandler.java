package spot.spot.global.stomp;

import static spot.spot.global.util.ConstantUtil.PERMIT_ALL;

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
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

	private final StompUtil stompUtil;

	@NonNull
	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		log.info("STOMP 접근 명령어={}", accessor.getCommand());
		String atk = stompUtil.getAccessToken(accessor);
		if(atk == null) throw new MessagingException("JWT TOKEN is NULL");
		switch (accessor.getCommand()) {
			case CONNECT :
				String msgType = accessor.getFirstNativeHeader("type");
				if(Objects.equals(msgType,PERMIT_ALL)) {break;}
			case SUBSCRIBE:
			case SEND:
				stompUtil.verifyAccessToken(atk);
				break;
		}

		return message;
	}
}
