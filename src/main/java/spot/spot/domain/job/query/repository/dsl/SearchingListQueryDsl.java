package spot.spot.domain.job.query.repository.dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;;
import com.querydsl.sql.SQLQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.entity.QCertification;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.job.query.dto.response.CertificationImgResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.dsl._docs.SearchingListQueryDocs;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.member.entity.QWorker;
import spot.spot.domain.member.entity.QWorkerAbility;
import spot.spot.domain.member.entity.Worker;


@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchingListQueryDsl implements SearchingListQueryDocs {  // java 코드로 쿼리문을 build 하는 방법

    private final JPAQueryFactory queryFactory;
    private final SQLQueryFactory sqlQueryFactory;
    private final QJob job = QJob.job;
    private final QWorker worker = QWorker.worker;
    private final QWorkerAbility workerAbility = QWorkerAbility.workerAbility;
    private final QMatching matching = QMatching.matching;
    private final QMember member = QMember.member;

    @Transactional(readOnly = true)
    public Slice<NearByJobResponse> findNearByJobsWithQueryDSL(double lat, double lng, double dist, Pageable pageable) {
        // MySQL 인식 용
        String pointWKT = "POINT(" + lat + " " + lng + ")";
        log.info(pointWKT);
        // 거리 계산 (MySQL 공간 함수 활용)
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
            "ST_Distance_Sphere(location, ST_GeomFromText({0}, 4326))",
            Expressions.constant(pointWKT));
        // QueryDSL 실행 (SPATIAL INDEX 사용)
        List<NearByJobResponse> jobs = sqlQueryFactory
            .select(Projections.constructor(
                NearByJobResponse.class,
                job.id,
                job.title,
                job.content,
                job.img.as("picture"),
                job.lat,
                job.lng,
                job.money,
                distanceExpression,
                job.tid
            ))
            .from(job)
            .where(
                Expressions.booleanTemplate(
                    "MBRContains(ST_Buffer(ST_GeomFromText({0}, 4326), {1}), location)",
                    Expressions.constant(pointWKT),
                    dist*1000
                )
            )
            .orderBy(distanceExpression.asc()) // 거리 기준 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1) // Slice 지원 위해 1개 더 조회
            .fetch();
        // 다음 페이지가 있는지 계산
        boolean hasNext = jobs.size() > pageable.getPageSize();
        if (hasNext) {
            jobs = jobs.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(jobs, pageable, hasNext);
    }


    public Slice<Worker> findWorkersByJobId(Long jobId, Pageable pageable) {
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

    @Override
    public List<CertificationImgResponse> findWorkersCertificationImgList(long jobId) {
        QCertification certification = QCertification.certification;

        return queryFactory
            .select(Projections.constructor(CertificationImgResponse.class,
                certification.img))
            .from(certification)
            .join(matching).on(certification.matching.id.eq(matching.id))
            .where(matching.job.id.eq(jobId).and(matching.status.notIn(MatchingStatus.OWNER, MatchingStatus.ATTENDER, MatchingStatus.REQUEST)))
            .fetch();
    }


    @Transactional(readOnly = true)
    public List<NearByJobResponse> findJobsforGeoHashSync() {
        // MySQL 인식 용
        // QueryDSL 실행 (SPATIAL INDEX 사용)
        return  sqlQueryFactory
            .select(Projections.constructor(
                NearByJobResponse.class,
                job.id,
                job.title,
                job.content,
                job.img.as("picture"),
                job.lat,
                job.lng,
                job.money,
                Expressions.constant(0.0),
                job.tid
            ))
            .from(job)
            .where(job.startedAt.isNull(), job.location.isNotNull())
            .fetch();
        // 다음 페이지가 있는지 계산
    }
}
