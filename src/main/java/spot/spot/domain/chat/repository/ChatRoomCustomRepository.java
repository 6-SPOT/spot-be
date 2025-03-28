package spot.spot.domain.chat.repository;

import java.util.Optional;

public interface ChatRoomCustomRepository {

	Optional<Long> findChatRoomId(Long member1Id, Long member2Id, Long jobId);
}
