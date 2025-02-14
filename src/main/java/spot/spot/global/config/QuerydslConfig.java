package spot.spot.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    @PersistenceContext                 // 현재 트랜잭션의 Entity Manager 주입
    private EntityManager entityManager;   // 쿼리 실행 및 영속성 컨텍스트를 관리하는 객체

    @Bean
    public JPAQueryFactory jpaQueryFactory() {return new JPAQueryFactory(entityManager);}   // JPQL 작성 객체 -> QueryDSL로 넘겨주겠다.
}
