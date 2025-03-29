package spot.spot.domain.pay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.pay.entity.PointStatus;
import spot.spot.domain.pay.entity.QPoint;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PointRepositoryDsl {

    private final JPAQueryFactory queryFactory;
    private final QPoint point = QPoint.point1;

    public int updatePointOptimistic(String pointCode, int oldCount, int newCount) {
        return (int) queryFactory.update(point)
                .set(point.count, newCount)
                .where(point.pointCode.eq(pointCode)
                        .and(point.count.eq(oldCount)))
                .execute();
    }

    public void expirePoint(String pointCode) {
        log.info("🔥 expirePoint 진입: {}", pointCode); // ← 여기부터 찍히는지 확인
        long count = queryFactory
                .update(point)
                .set(point.pointStatus, PointStatus.EXPIRED)
                .set(point.count, 0)
                .where(point.pointCode.eq(pointCode)
                        .and(point.pointStatus.ne(PointStatus.EXPIRED)))
                .execute();
        log.info("🔥 expirePoint updated count: {}", count);
    }
}
