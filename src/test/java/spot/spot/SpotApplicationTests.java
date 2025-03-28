package spot.spot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
@ActiveProfiles("local")
class SpotApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private DataSource dataSource;

	@Test
	void checkDatabaseUrl() throws Exception {
		Connection connection = dataSource.getConnection();
		String dbUrl = connection.getMetaData().getURL();
		System.out.println("현재 연결된 데이터베이스 URL: " + dbUrl);
	}
}
