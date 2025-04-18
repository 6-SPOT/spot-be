package spot.spot.domain.job.command.repository.dsl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.job.command.repository.dsl._docs.ChangeJobStatusCommandDocs;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.member.entity.QWorker;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChangeJobStatusCommandDsl implements ChangeJobStatusCommandDocs {

    private final JPAQueryFactory queryFactory;
    private final QJob job = QJob.job;
    private final QMember member = QMember.member;
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
                        .exists()
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
                        .exists()// 구직자 등록 되었는가?
                )
                .fetchOne()
        ).orElseThrow(() -> new GlobalException(ErrorCode.DIDNT_PASS_VALIDATION));
    }

    public void findJobWithValidation(long worker_id, long job_id, MatchingStatus expected_status1, MatchingStatus expected_status2) {
        Optional.ofNullable(
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
                            .and(matching.status.in(expected_status1, expected_status2)))
                        .exists()
                )
                .fetchOne()
        ).orElseThrow(() -> new GlobalException(ErrorCode.DIDNT_PASS_VALIDATION));
    }

    public  Matching updateMatchingStatus(long worker_id, long job_id, MatchingStatus next) {
        long affectedRows = queryFactory.update(matching)
            .set(matching.status, next)
            .where(matching.job.id.eq(job_id), matching.member.id.eq(worker_id))
            .execute();

        if (affectedRows == 0) {
            throw new GlobalException(ErrorCode.FAILED_2_UPDATE_JOB_STATUS);
        }

        return queryFactory.selectFrom(matching)
            .where(matching.job.id.eq(job_id), matching.member.id.eq(worker_id))
            .fetchOne();
    }

    public void updateMatchingStatus (long matching_id, MatchingStatus next) {
        if(queryFactory.update(matching)
            .set(matching.status, next)
            .where(matching.id.eq(matching_id))
            .execute() == 0) throw new GlobalException(ErrorCode.FAILED_2_UPDATE_JOB_STATUS);
    }

    @Override
    public Member getJobsOwner(long jobId) {
        return queryFactory
            .select(member)
            .from(member)
            .join(matching)
            .on(member.id.eq(matching.member.id))
            .where(matching.status.eq(MatchingStatus.OWNER).and(matching.job.id.eq(jobId)))
            .fetchOne();
    }
}
