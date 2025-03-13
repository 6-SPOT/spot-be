package spot.spot.domain.job.query.repository.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.member.entity.QWorker;
import spot.spot.domain.member.entity.QWorkerAbility;
import spot.spot.domain.member.entity.Worker;

@Repository
@RequiredArgsConstructor
public class SearchingListQueryDsl {  // java 코드로 쿼리문을 build 하는 방법

    private final JPAQueryFactory queryFactory;
    private final QJob job = QJob.job;
    private final QWorker worker = QWorker.worker;
    private final QWorkerAbility workerAbility = QWorkerAbility.workerAbility;
    private final QMatching matching = QMatching.matching;
    private final QMember member = QMember.member;

    public Slice<Job> findNearByJobsWithQueryDSL(double lat, double lng, double dist, Pageable pageable) {
        QJob job = QJob.job;

        // Haversine을 이용한 거리 계산
        // Expressions.numberTemplate = queryDsl에서 숫자 계산에 쓰는 양식 -> sql 수식을 사용하면서도 Java 코드로서 사용 가능함.
        // NumberExpression<Double> = double 값을 반환함을 명시
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
            "(6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5}))))",
            lat, job.lat, job.lng, lng, lat, job.lat
            );
        final  double rangeFilter = dist / (111.045 * Math.cos(Math.toRadians(lat)));

        List<Job> jobs = queryFactory
            .select(job)
            .from(job)
            .where(
                job.startedAt.isNull(),
                job.lat.between(lat - (dist / 111.045), lat + (dist / 111.045)),
                job.lng.between(lng - rangeFilter, lng + rangeFilter),
                distanceExpression.lt(dist)
            )
            .orderBy(distanceExpression.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1) // 한 개 더 확인해서 다음 페이지가 있는지 확인
            .fetch();

        // 다음 페이지가 있는지 계산
        // QueryDsl은 page 객체는 자동 지원하지만, Slice 객체는 지원하지 않음.
        // 우리는 APP 용 무한 스크롤을 구현해야함으로, Slice를 사용해야함.
        // N+1개가 불러와진다. -> 다음이 존재한다. N개 이하로 불러와진다. -> 다음 페이지가 없다.
        boolean hasNext = jobs.size() > pageable.getPageSize();
        if(hasNext) {
            // 다음 페이지가 있으면 찾은 게 11개니 10개로 짤라서 반환
            jobs = jobs.subList(0, pageable.getPageSize());
        }
        // Slice 인터페이스의 구현체 (JPA에서 제공). <내용물, pageable 객체, 다음이 있는지 여부>를 주면 된다.
        return new SliceImpl<>(jobs, pageable, hasNext);
    }

    public Slice<Worker> findWorkersByJobIdAndStatus(Long jobId, Pageable pageable) {
        List<Worker> workers = queryFactory
            .selectFrom(worker)
            .join(worker.member, member).fetchJoin()
            .join(matching).on(matching.member.eq(member))
            .leftJoin(worker.workerAbilities, workerAbility)
            .where(
                matching.job.id.eq(jobId),
                matching.status.eq(MatchingStatus.ATTENDER)
            )
            .distinct() // ✅ 중복 제거
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();


        boolean hasNext = workers.size() > pageable.getPageSize();
        if (hasNext) {
            // Slice에서 마지막 요소 제거
            workers.remove(workers.size() - 1);
        }

        return new SliceImpl<>(workers, pageable, hasNext);
    }

    public List<JobSituationResponse> findJobSituationsByOwner(long memberId) {

        JPQLQuery<Long> subQuery = JPAExpressions
            .select(matching.job.id)
            .from(matching)
            .where(matching.member.id.eq(memberId)
                .and(matching.status.eq(MatchingStatus.OWNER))
            );

        BooleanExpression hasApplicants = JPAExpressions
            .selectOne()
            .from(QMatching.matching)
            .where(QMatching.matching.job.id.eq(job.id)
                .and(QMatching.matching.status.ne(MatchingStatus.OWNER)))
            .exists();

        // 참가자가 없는 경우 -> OWNER의 레코드를 그대로 띄움. 참가자가 한 명이라도 있다면? Owner인 레코드는 지우고 참가자 레코드만 띄움
        BooleanExpression condition = hasApplicants.not().or(matching.status.ne(MatchingStatus.OWNER));

        return queryFactory
            .select(Projections.constructor(JobSituationResponse.class,
                job.id.as("jobId"),
                job.title,
                job.img,
                job.content,
                matching.status,
                member.id.as("memberId"),
                member.nickname,
                member.phone,
                Expressions.constant(true)
            ))
            .from(job)
            .leftJoin(matching).on(job.id.eq(matching.job.id))
            .leftJoin(member).on(member.id.eq(matching.member.id))
            .where(job.id.in(subQuery)
                .and(condition)
            )
            .fetch();
    }

    public List<JobSituationResponse> findJobSituationsByWorker(long memberId) {
        return queryFactory
            .select(Projections.constructor(JobSituationResponse.class,
                job.id,
                job.title,
                job.img,
                job.content,
                matching.status,
                member.id,
                member.nickname,
                member.phone,
                Expressions.constant(false)
            ))
            .from(job)
            .leftJoin(matching).on(job.id.eq(matching.job.id))
            .leftJoin(member).on(member.id.eq(matching.member.id))
            .where(
                member.id.eq(memberId),
                matching.status.ne(MatchingStatus.OWNER) // NULL 값 처리 추가
            )
            .fetch();
    }

}
