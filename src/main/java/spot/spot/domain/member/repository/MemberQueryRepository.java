package spot.spot.domain.member.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.member.dto.request.MemberRequest;
import spot.spot.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static spot.spot.domain.member.entity.QMember.member;
import static spot.spot.domain.member.entity.QWorker.worker;

@Repository
@RequiredArgsConstructor
@Transactional
public class MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMatching matching = QMatching.matching;

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

    public Optional<Member> findMemberByMatchingOwner(Job job) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(member)
                        .from(matching)
                        .innerJoin(member).on(matching.member.id.eq(member.id))
                        .where(
                                matching.job.id.eq(job.getId())
                                        .and(matching.status.eq(MatchingStatus.OWNER))
                        )
                        .fetchOne()
        );
    }

    public Optional<Member> findOneFinisherOfJob (long jobId) {
        return Optional.ofNullable(
            jpaQueryFactory
            .select(member)
            .from(matching)
            .join(member).on(member.id.eq(matching.member.id))
            .where(matching.job.id.eq(jobId).and(matching.status.eq(MatchingStatus.FINISH)))
            .limit(1)
            .fetchOne());
    }
}
