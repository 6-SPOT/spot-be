package spot.spot.global.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.security.util.JwtUtil;

import static spot.spot.global.util.ConstantUtil.AUTHORIZATION;
import static spot.spot.global.util.ConstantUtil.AUTH_ERROR;

@Component
@RequiredArgsConstructor
public class StompUtil {

    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    public String getAccessToken(StompHeaderAccessor accessor) {
        return jwtUtil.separateBearer(accessor.getFirstNativeHeader(AUTHORIZATION));
    }

    public void verifyAccessToken(String token) throws MessagingException {
        ErrorCode errorCode = jwtUtil.validateToken(token);
        if(errorCode != null) {
            throw  new MessagingException(errorCode.getMessage());
        }
    }

    public Message<?> handleErrorMessage(StompHeaderAccessor accessor, String errorMessage) {
        accessor.setLeaveMutable(true);
        accessor.setHeader(AUTH_ERROR, AUTH_ERROR);

        String sessionId = accessor.getSessionId();
        brokerMessagingTemplate.convertAndSendToUser(sessionId, "/error", errorMessage);

        return MessageBuilder.withPayload(errorMessage)
            .setHeaders(accessor)
            .build();
    }
}
