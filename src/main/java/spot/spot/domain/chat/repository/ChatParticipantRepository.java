package spot.spot.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import spot.spot.domain.chat.entity.ChatParticipant;
import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.member.entity.Member;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
	List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

	List<ChatParticipant> findAllByMember(Member member);
}
