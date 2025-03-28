package spot.spot.domain.pay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.pay.entity.QPoint;

@Repository
@RequiredArgsConstructor
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
}
