package spot.spot.domain.chat.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.domain.member.entity.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chat_participants")
@Comment("채팅 참여자")
public class ChatParticipant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("채팅 참여자 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	@Comment("채팅방 아이디")
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	@Comment("회원 아이디")
	private Member member;
}
