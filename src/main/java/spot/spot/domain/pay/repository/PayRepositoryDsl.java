package spot.spot.domain.pay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.pay.entity.QPayHistory;

@Repository
@RequiredArgsConstructor
public class PayRepositoryDsl {

    private final JPAQueryFactory queryFactory;
    private final QPayHistory payHistory = QPayHistory.payHistory;
    private final QMember member = QMember.member;
    private final QMatching matching = QMatching.matching;

    public Integer findByPayAmountFromMatchingJob(Long matchingId, Long workerId) {
        return queryFactory
                .select(payHistory.payAmount)
                .from(payHistory)
                .join(matching).on(payHistory.job.id.eq(matching.job.id))
                .join(member).on(member.id.eq(matching.member.id))
                .where(matching.id.eq(matchingId)
                        .and(member.id.eq(workerId)))
                .fetchOne();
    }
}
