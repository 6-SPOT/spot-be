package spot.spot.domain.job.v2.query.repository.dsl;

import com.querydsl.core.JoinFlag.Position;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.querydsl.sql.SQLQueryFactory;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Deprecated
@Repository
@RequiredArgsConstructor
public class SearchingListQueryDslv2 {
    private final QJob job = QJob.job;
    private final SQLQueryFactory sqlQueryFactory;

    @Transactional(readOnly = true)
    public Slice<NearByJobResponse> findNearByJobsWithQueryDSL(double lat, double lng, double dist, Pageable pageable) {
        // ✅ Haversine 거리 계산 수식
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
            "(6371 * acos(cos(radians({0})) * cos(radians(job.lat)) * cos(radians(job.lng) - radians({1})) + sin(radians({2})) * sin(radians(job.lat))))",
            lat, lng, lat
        );

        final double rangeFilter = dist / (111.045 * Math.cos(Math.toRadians(lat)));

        // ✅ 1️⃣ 서브쿼리 생성 (idx_lat_lng 인덱스 강제 적용)
        QJob subJob = new QJob("subJob");

        var subQuery = sqlQueryFactory
            .select(
                job.id,
                job.title,
                job.content,
                job.img,
                job.lat,
                job.lng,
                job.money,
                job.tid,
                distanceExpression.as("dist") // ✅ 거리 계산 후 별칭(dist) 적용
            )
            .from(job)
            .addJoinFlag(" FORCE INDEX (idx_lat_lng)", Position.END) // ✅ 인덱스 강제 적용
            .where(
                Expressions.stringPath("started_at").isNull(),
                job.lat.between(lat - (dist / 111.045), lat + (dist / 111.045)),
                job.lng.between(lng - rangeFilter, lng + rangeFilter)
            );

        List<NearByJobResponse> jobs = sqlQueryFactory
            .select(Projections.constructor(
                NearByJobResponse.class,
                subJob.id,
                subJob.title,
                subJob.content,
                subJob.img.as("picture"),
                subJob.lat,
                subJob.lng,
                subJob.money,
                Expressions.numberPath(Double.class, "dist"), // ✅ 서브쿼리의 `dist` 사용
                subJob.tid
            ))
            .from(subQuery, subJob) // ✅ 서브쿼리 적용
            .having(Expressions.numberPath(Double.class, "dist").loe(dist)) // ✅ 거리 필터링을 HAVING으로 변경
            .orderBy(Expressions.numberPath(Double.class, "dist").asc()) // ✅ 거리순 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = jobs.size() > pageable.getPageSize();
        if (hasNext) {
            jobs = jobs.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(jobs, pageable, hasNext);
    }

}
