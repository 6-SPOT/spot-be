package spot.spot.domain.notification.query.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import spot.spot.domain.notification.query.dto.response.NotificationResponse;

@Tag(name="6. NOTIFICATION QUERY API", description = "<br/> 알림 조회 API")
public interface NotificationQueryControllerDocs {
    @Operation(summary = "내 알림 목록의 최신순을 가져오는 API", description = "page = 페이지 번호, size = 한 페이지 당 알림 개수")
    public Slice<NotificationResponse> getMyNotificationList(Pageable pageable);
}
