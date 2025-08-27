package spot.spot.domain.notification.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.notification.query.controller._docs.NotificationQueryControllerDocs;
import spot.spot.domain.notification.query.dto.response.NotificationResponse;
import spot.spot.domain.notification.query.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationQueryController implements NotificationQueryControllerDocs {

    private final NotificationService notificationService;

    @GetMapping("/my-list")
    public Slice<NotificationResponse> getMyNotificationList(Pageable pageable) {
        return notificationService.getMyNotificationList(pageable);
    }
}
