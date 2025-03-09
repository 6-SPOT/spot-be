package spot.spot.domain.job.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.dto.response.JobDetailResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.entity.QJob;
import spot.spot.domain.job.entity.QMatching;

import java.util.Optional;
import spot.spot.domain.member.entity.QMember;

@Repository
@RequiredArgsConstructor
public class MatchingDsl {

    private final JPAQueryFactory queryFactory;
    private final QMember member = QMember.member;
    private final QJob job = QJob.job;
    private final QMatching matching = QMatching.matching;
    public Optional<String> findWorkerNicknameByJob(Job job) {

        return Optional.ofNullable(
                queryFactory
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

    public Optional<JobDetailResponse> findOneJobDetail(long jobId, long memberId) {
        QMatching myMatching = new QMatching("my_matching"); // 별칭 생성
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(JobDetailResponse.class,
                job.id,
                job.title,
                job.content,
                job.img,
                job.lat,
                job.lng,
                job.money,
                job.tid,
                member.id,
                member.nickname,
                member.img,
                myMatching.status
            ))
            .from(job)
            .join(matching).on(job.id.eq(matching.job.id))
            .join(member).on(matching.member.id.eq(member.id))
            .leftJoin(myMatching)
            .on(myMatching.job.id.eq(jobId)
                .and(myMatching.member.id.eq(memberId)))
            .where(matching.status.eq(MatchingStatus.OWNER)
                .and(job.id.eq(jobId)))
            .fetchOne());
    }
}
