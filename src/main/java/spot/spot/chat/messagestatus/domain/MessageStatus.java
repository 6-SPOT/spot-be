package spot.spot.chat.messagestatus.domain;

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
import spot.spot.chat.chatroom.domain.ChatRoom;
import spot.spot.chat.message.domain.Message;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "message_status")
@Comment("메세지 상태")
public class MessageStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("메세지 상태 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	@Comment("채팅방 아이디")
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	@Comment("회원 아이디")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", nullable = false)
	@Comment("메세지 아이디")
	private Message message;

	@Comment("읽음 여부")
	private boolean isRead;
}
