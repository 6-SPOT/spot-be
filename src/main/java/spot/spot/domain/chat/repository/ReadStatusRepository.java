package spot.spot.domain.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.chat.entity.ReadStatus;
import spot.spot.domain.member.entity.Member;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
	List<ReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

	Long countByChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);
}
