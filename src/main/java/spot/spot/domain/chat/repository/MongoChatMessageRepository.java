package spot.spot.domain.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import spot.spot.domain.chat.mongodb.MongoChatMessage;

public interface MongoChatMessageRepository extends MongoRepository<MongoChatMessage, String> {
	List<MongoChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
