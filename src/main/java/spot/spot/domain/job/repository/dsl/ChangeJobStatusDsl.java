package spot.spot.domain.job.repository.dsl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.entity.QJob;
import spot.spot.domain.job.entity.QMatching;
import spot.spot.domain.member.entity.QWorker;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChangeJobStatusDsl {

    private final JPAQueryFactory queryFactory;
    private final QJob job = QJob.job;
    private final QWorker worker = QWorker.worker;
    private final QMatching matching = QMatching.matching;

    public Job findJobWithValidation(long worker_id, long job_id, MatchingStatus expected_status) {
        return Optional.ofNullable(
            queryFactory
                .select(job)
                .from(job)
                .where(
                    job.id.eq(job_id),
                    JPAExpressions.selectOne()   // 구직자로 등록되어졌는지
                        .from(worker)
                        .where(worker.member.id.eq(worker_id))
                        .exists(),
                    JPAExpressions.selectOne()  // 예상하는 전 단계가 맞는지
                        .from(matching)
                        .where(matching.job.id.eq(job_id)
                            .and(matching.member.id.eq(worker_id))
                            .and(matching.status.eq(expected_status)))
                        .exists(),
                    JPAExpressions.selectOne()  // 중복 매칭 검증
                        .from(matching)
                        .where(matching.job.id.eq(job_id), matching.member.id.eq(worker_id))
                        .groupBy(matching.job.id, matching.member.id)
                        .having(matching.id.count().gt(1))
                        .notExists()
                )
                .fetchOne()
        ).orElseThrow(() -> new GlobalException(ErrorCode.DIDNT_PASS_VALIDATION));
    }

    public Job findJobWithValidation(long worker_id, long job_id) {
        return Optional.ofNullable(
            queryFactory
                .select(job)
                .from(job)
                .where(
                    job.id.eq(job_id),
                    JPAExpressions.selectOne()
                        .from(worker)
                        .where(worker.member.id.eq(worker_id))
                        .exists(),// 구직자 등록 되었는가?
                    JPAExpressions.selectOne()
                        .from(matching)
                        .where(
                            matching.job.id.eq(job_id),
                            matching.member.id.eq(worker_id),
                            matching.status.eq(MatchingStatus.OWNER)
                        )
                        .notExists(),
                    JPAExpressions.selectOne()  // 중복 매칭 검증
                        .from(matching)
                        .where(matching.job.id.eq(job_id), matching.member.id.eq(worker_id))
                        .groupBy(matching.job.id, matching.member.id)
                        .having(matching.id.count().gt(1))
                        .notExists()
                )
                .fetchOne()
        ).orElseThrow(() -> new GlobalException(ErrorCode.DIDNT_PASS_VALIDATION));
    }

    public  void updateMatchingStatus(long worker_id, long job_id, MatchingStatus next) {
        if (queryFactory.update(matching)
            .set(matching.status, next)
            .where(matching.job.id.eq(job_id), matching.member.id.eq(worker_id))
            .execute() == 0) {
            throw new GlobalException(ErrorCode.FAILED_2_UPDATE_JOB_STATUS);
        }
    }
}
