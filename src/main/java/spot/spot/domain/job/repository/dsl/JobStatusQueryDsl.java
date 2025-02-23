package spot.spot.domain.job.repository.dsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.dto.response.JobWithOwnerAndErrorCodeResponse;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.entity.QJob;
import spot.spot.domain.job.entity.QMatching;
import spot.spot.domain.job.mapper.Job4WorkerMapper;
import spot.spot.domain.member.entity.QWorker;
import spot.spot.global.response.format.ErrorCode;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JobStatusQueryDsl {

    private final JPAQueryFactory queryFactory;
    private final Job4WorkerMapper job4WorkerMapper;

    public Optional<JobWithOwnerAndErrorCodeResponse> findJowWithOwnerAndErrorCode (long attenderId, long jobId) {
        QJob job = QJob.job;
        QMatching ownerMatching = new QMatching("ownerMatching") ;

            log.info("Query Result {}", queryFactory
                .select(
                    job,
                    ownerMatching.member.id,
                    new CaseBuilder()
                        .when(job.startedAt.isNotNull()).then(1) // 해당 일은 이미 시작함
                        .when(JPAExpressions.selectOne()
                            .from(QWorker.worker)
                            .where(QWorker.worker.member.id.eq(attenderId))
                            .exists().not())
                        .then(2)
                        .when(JPAExpressions.selectOne()
                            .from(QMatching.matching)
                            .where(QMatching.matching.job.id.eq(jobId)
                                .and(QMatching.matching.member.id.eq(attenderId))
                                .and(QMatching.matching.status.eq(MatchingStatus.ATTENDER)))
                            .exists()).then(3) // 이미 해당 일의 참여자로 있음.
                        .otherwise(4)
                )
                .from(job)
                .leftJoin(ownerMatching)
                .on(ownerMatching.job.eq(job).and(ownerMatching.status.eq(MatchingStatus.OWNER)))   // 해당 일의 주인임
                .where(job.id.eq(jobId))
                .fetchOne());

        return Optional.ofNullable(queryFactory
                .select(
                    job,
                    ownerMatching.member.id,
                    new CaseBuilder()
                        .when(job.startedAt.isNotNull()).then(1) // 해당 일은 이미 시작함
                        .when(JPAExpressions.selectOne()
                            .from(QWorker.worker)
                            .where(QWorker.worker.member.id.eq(attenderId))
                            .exists().not())
                        .then(2)
                        .when(JPAExpressions.selectOne()
                            .from(QMatching.matching)
                            .where(QMatching.matching.job.id.eq(jobId)
                                .and(QMatching.matching.member.id.eq(attenderId))
                                .and(QMatching.matching.status.eq(MatchingStatus.ATTENDER)))
                            .exists()).then(3) // 이미 해당 일의 참여자로 있음.
                        .otherwise(4)
                )
                .from(job)
                .leftJoin(ownerMatching)
                .on(ownerMatching.job.eq(job).and(ownerMatching.status.eq(MatchingStatus.OWNER)))   // 해당 일의 주인임
                .where(job.id.eq(jobId))
                .fetchOne())
            .map(job4WorkerMapper::toJobWithOwnerAndErrorCodeResponse);
    }
}
