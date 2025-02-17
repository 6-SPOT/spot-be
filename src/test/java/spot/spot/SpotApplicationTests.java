package spot.spot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.data.redis.enabled=false")
class SpotApplicationTests {

	@Test
	void contextLoads() {
	}

}
