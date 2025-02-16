package spot.spot.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.notification.dto.request.UpdateFcmTokenRequest;
import spot.spot.domain.notification.service.FcmService;


@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {
    private final FcmService fcmService;

    @PostMapping("/save-token")
    public void saveFcmToken( @RequestBody UpdateFcmTokenRequest request) {
        fcmService.saveFcmToken(request);
    }
}
