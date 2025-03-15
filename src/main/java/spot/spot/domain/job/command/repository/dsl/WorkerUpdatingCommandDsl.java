package spot.spot.domain.job.command.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.QMember;

@Repository
@RequiredArgsConstructor
public class WorkerUpdatingCommandDsl {
    private final JPAQueryFactory queryFactory;
    private QMember member = QMember.member;

    public void updateLocationById(long memberId, double lat, double lng, Point location) {
        queryFactory.update(member)
            .set(member.lat, lat)
            .set(member.lng, lng)
            .set(member.location, location)
            .where(member.id.eq(memberId))
            .execute();
    }
}
