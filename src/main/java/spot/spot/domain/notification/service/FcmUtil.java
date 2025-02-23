package spot.spot.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.dto.response.FcmDTO;
import spot.spot.domain.notification.entity.FcmToken;
import spot.spot.domain.notification.repository.FcmTokenRepository;
import spot.spot.global.logging.ColorLogger;
import spot.spot.global.response.format.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmUtil  {
    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;
    // 회원 한 명과 관련된 FCM 토큰에 메시지를 보내는 기능
    @Async("taskExecutor")
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

    public void sendMessage(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("fcm send error");
            ColorLogger.red(e.getMessage());
            e.getStackTrace();
        }
    }

    public FcmDTO makeFcmDTO(String title, String body) {
        return FcmDTO.builder()
            .title(title)
            .body(body)
            .build();
    }

    public String makeRequestingJobBody(String attenderName, String jobName){
        return attenderName + "님이 " + jobName + "을 해결하길 원합니다!";
    }

}
