package spot.spot.domain.notification.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.query.dto.response.NotificationResponse;
import spot.spot.domain.notification.query.repository.SearchingNotificationListDsl;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserAccessUtil userAccessUtil;
    private final SearchingNotificationListDsl searchingNotificationListDsl;

    public Slice<NotificationResponse> getMyNotificationList(Pageable pageable) {
        Member me = userAccessUtil.getMember();
        return searchingNotificationListDsl.getMyNotificationList(me.getId(), pageable);
    }
}
