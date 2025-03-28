package spot.spot.domain.chat.repository;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import spot.spot.domain.chat.mongodb.MongoChatMessage;

@Repository
@Profile("kafka")
public interface MongoChatMessageRepository extends MongoRepository<MongoChatMessage, String> {
	List<MongoChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
