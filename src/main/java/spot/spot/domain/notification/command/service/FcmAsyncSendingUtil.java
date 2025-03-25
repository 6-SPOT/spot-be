package spot.spot.domain.notification.command.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
import spot.spot.domain.notification.command.entity.FcmToken;
import spot.spot.domain.notification.command.repository.FcmTokenRepository;
import spot.spot.global.logging.ColorLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmAsyncSendingUtil {
    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;
    // 회원 한 명과 관련된 FCM 토큰에 메시지를 보내는 기능

    @Async("taskExecutor")
    @Retryable(
        retryFor = RejectedExecutionException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000)
    )
    public void singleFcmSend(long receiverId,  FcmDTO fcmDTO) {
        fcmTokenRepository.findAllByMember_Id(receiverId)
            .stream()
            .map(FcmToken::getData)
            .map(token -> makeMessage(token, fcmDTO))
            .forEach(this::sendMessage);
    }

    @Async("taskExecutor")
    public void multiFcmSend(List<Member> members, FcmDTO fcmDTO) {
        members.forEach(member -> singleFcmSend(member.getId(), fcmDTO));
    }


    private Message makeMessage(String token, FcmDTO fcmDTO) {
        log.info(token);
        Notification.Builder notificationBuilder =
            Notification.builder()
                .setTitle(fcmDTO.title())
                .setBody(fcmDTO.body());

        return Message.builder()
            .setNotification(notificationBuilder.build())
            .setToken(token)
            .build();
    }

    private void sendMessage(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("fcm send error");
            ColorLogger.red(e.getMessage());
            e.getStackTrace();
        }
    }

}
