package spot.spot.domain.chat.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import spot.spot.global.config.QuerydslConfig;

@DataJpaTest
@Import({QuerydslConfig.class})
class ChatRoomRepositoryTest {

}
