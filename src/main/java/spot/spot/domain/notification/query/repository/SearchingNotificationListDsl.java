package spot.spot.domain.notification.query.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.notification.command.entity.QNotification;
import spot.spot.domain.notification.query.dto.response.NotificationResponse;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchingNotificationListDsl {
    private final JPAQueryFactory queryFactory;
    private final QNotification notification = QNotification.notification;
    private final QMember member = QMember.member;


    public Slice<NotificationResponse> getMyNotificationList(long memberId, Pageable pageable) {
        List<NotificationResponse> list
            = queryFactory
            .select(Projections.constructor(NotificationResponse.class,
                notification.id,
                notification.createdAt,
                notification.content,
                notification.member.id,
                member.nickname,
                member.img,
                notification.type
            ))
            .from(notification)
            .join(member)
            .on(member.id.eq(notification.receiverId))
            .where(member.id.eq(memberId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(notification.createdAt.desc())
            .fetch();
        // 페이지 사이즈가 10개면 내꺼는 11개 가져와서 뒷 페이지가 있는지 체크함.
        boolean hasNext = list.size() > pageable.getPageSize();
        if(hasNext) {list.remove(list.size() -1);}

        return new SliceImpl<>(list, pageable, hasNext);
    }

}
