package spot.spot.domain.chat.entity;

import org.hibernate.annotations.Comment;
import org.web3j.abi.datatypes.Bool;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spot.spot.domain.member.entity.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "message_status")
@Comment("메세지 상태")
public class ReadStatus {

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
	private ChatMessage chatMessage;

	@Comment("읽음 여부")
	@Setter
	private Boolean isRead;

	@Builder
	public ReadStatus(ChatRoom chatRoom, Member member, ChatMessage chatMessage, Boolean isRead) {
		this.chatRoom = chatRoom;
		this.member = member;
		this.chatMessage = chatMessage;
		this.isRead = (isRead != null) ? isRead : false;
	}
}
