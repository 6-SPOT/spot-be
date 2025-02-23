package spot.spot.domain.job.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobStatusQueryDsl {

    private final JPAQueryFactory factory;



}
