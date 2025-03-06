package spot.spot.domain.pay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.QJob;
import spot.spot.domain.pay.entity.QPayHistory;

@Repository
@RequiredArgsConstructor
public class PayQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QPayHistory payHistory = QPayHistory.payHistory;
    private final QJob job = QJob.job;

    public Integer findPayAmountByPayHistory(Long jobId) {

        return queryFactory
                .select(payHistory.payAmount)
                .from(job)
                .where(job.id.eq(jobId))
                .fetchOne();
    }

}
