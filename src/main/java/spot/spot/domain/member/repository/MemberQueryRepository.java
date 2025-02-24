package spot.spot.domain.member.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.entity.Member;

import java.util.List;

import static spot.spot.domain.member.entity.QMember.member;
import static spot.spot.domain.member.entity.QWorker.worker;

@Repository
@RequiredArgsConstructor
@Transactional
public class MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public void updateMember(Long memberId, MemberRequest.modify modify) {
        jpaQueryFactory.update(member)
                .set(member.email, modify.getEmail())
                .set(member.lat, modify.getLat())
                .set(member.lng, modify.getLng())
                .set(member.phone, modify.getPhone())
                .set(member.nickname, modify.getNickname())
                .set(member.img, modify.getImg())
                .where(member.id.eq(memberId))
                .execute();
    }

    public List<Member> findWorkerNearByMember(double lat, double lng, double dist) {
        var distanceExpression = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                lat, member.lat, member.lng, lng
        );

        return jpaQueryFactory
                .select(member)
                .from(member)
                .innerJoin(worker).on(member.id.eq(worker.member.id))
                .where(distanceExpression.lt(dist))
                .orderBy(distanceExpression.asc())
                .fetch();
    }
}
