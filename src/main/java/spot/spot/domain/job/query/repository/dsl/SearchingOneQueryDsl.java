package spot.spot.domain.job.query.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.job.query.dto.response.JobDetailResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.MatchingStatus;
import java.util.Optional;
import spot.spot.domain.member.entity.QMember;

@Repository
@RequiredArgsConstructor
public class SearchingOneQueryDsl {

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
                myMatching.status,
                new CaseBuilder()
                    .when(member.id.eq(memberId)) // member.id가 memberId와 같으면 true
                    .then(true)
                    .otherwise(false)
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
