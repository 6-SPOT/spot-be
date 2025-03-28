package spot.spot.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    @PersistenceContext                 // 현재 트랜잭션의 Entity Manager 주입
    private EntityManager entityManager;   // 쿼리 실행 및 영속성 컨텍스트를 관리하는 객체

    private final DataSource dataSource;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {return new JPAQueryFactory(entityManager);}   // JPQL 작성 객체 -> QueryDSL로 넘겨주겠다.

    @Bean
    public SQLQueryFactory sqlQueryFactory() {
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(new MySQLTemplates()); // ✅ MySQL 전용 템플릿
        return new SQLQueryFactory(configuration, new SpringConnectionProvider(dataSource));
    }
}
