package spot.spot.domain.job.v1.query.repository.dsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.QJob;

@Repository
@Deprecated
@RequiredArgsConstructor
public class SearchingListQueryDslV1 {

    private final JPAQueryFactory queryFactory;

    public Slice<Job> findNearByJobsWithQueryDSLVersion1(double lat, double lng, double dist, Pageable pageable) {
        QJob job = QJob.job;

        // Haversine을 이용한 거리 계산
        // Expressions.numberTemplate = queryDsl에서 숫자 계산에 쓰는 양식 -> sql 수식을 사용하면서도 Java 코드로서 사용 가능함.
        // NumberExpression<Double> = double 값을 반환함을 명시
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
            "(6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({4})) * sin(radians({5}))))",
            lat, job.lat, job.lng, lng, lat, job.lat
        );

        List<Job> jobs = queryFactory
            .select(job)
            .from(job)
            .where(
                job.startedAt.isNull(),
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
}
