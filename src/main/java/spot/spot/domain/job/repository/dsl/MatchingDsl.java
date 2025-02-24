package spot.spot.domain.job.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.entity.QMatching;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MatchingDsl {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<String> findWorkerNicknameByJob(Job job) {
        QMatching matching = QMatching.matching;

        return Optional.ofNullable(
                jpaQueryFactory
                        .select(matching.member.nickname)
                        .from(matching)
                        .where(
                                matching.job.eq(job)
                                        .and(matching.status.eq(MatchingStatus.YES))
                        )
                        .orderBy(matching.id.asc())
                        .fetchFirst()
        );
    }
}
