package spot.spot.domain.notification.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.notification.command.controller._docs.NotificationCommandDocs;
import spot.spot.domain.notification.command.dto.request.FcmTestRequest;
import spot.spot.domain.notification.command.dto.request.UpdateFcmTokenRequest;
import spot.spot.domain.notification.command.service.FcmService;


@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class NotificationCommandController implements NotificationCommandDocs {
    private final FcmService fcmService;

    @PostMapping("/save-token")
    public void saveFcmToken( @RequestBody UpdateFcmTokenRequest request) {
        fcmService.saveFcmToken(request);
    }

    @PostMapping("/test-sending")
    public void testSend(@RequestBody FcmTestRequest request) {
        fcmService.testSending(request);
    }
}
