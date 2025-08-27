package spot.spot.domain.notification.command.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import spot.spot.domain.notification.command.dto.request.FcmTestRequest;
import spot.spot.domain.notification.command.dto.request.UpdateFcmTokenRequest;

@Tag(name = "5. NOTIFICATION COMMAND API", description = "<br/> 프론트가 FCM 알림을 쓰기 위한 API")
public interface NotificationCommandDocs {
    @Operation(summary = "FCM 토큰을 저장하는 API", description = "FCM 알림 메시지를 받으려면 이걸 받아야 함.")
    @PostMapping
    public void saveFcmToken( @RequestBody UpdateFcmTokenRequest request);

    @Operation(summary = "FCM 알림이 되는지 테스트 API", description = "보낼 사람 id랑 메시지 보내주시면 됩니다.")
    @PostMapping
    public void testSend(@RequestBody FcmTestRequest request);
}
