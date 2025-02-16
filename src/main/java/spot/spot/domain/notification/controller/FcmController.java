package spot.spot.domain.notification.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/fcm")
public class FcmController {

    @PostMapping("/save-token")
    public void saveFcmToken() {

    }
}
