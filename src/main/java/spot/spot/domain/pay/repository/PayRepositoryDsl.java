package spot.spot.domain.pay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.QJob;
import spot.spot.domain.job.entity.QMatching;
import spot.spot.domain.pay.entity.QPayHistory;

@Repository
@RequiredArgsConstructor
public class PayRepositoryDsl {

    private final JPAQueryFactory queryFactory;
    private final QPayHistory payHistory = QPayHistory.payHistory;
    private final QJob job = QJob.job;
    private final QMatching matching = QMatching.matching;

    public Integer findByPayAmountFromMatchingJob(Long matchingId) {
        return queryFactory
                .select(payHistory.payAmount)
                .from(payHistory)
                .join(payHistory.job, job)
                .join(matching).on(matching.job.id.eq(job.id))
                .fetchOne();
    }
}
