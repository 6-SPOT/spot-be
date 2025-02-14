package spot.spot.domain.chat.entity;

import java.time.LocalDateTime;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.domain.member.entity.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "messages")
@Comment("메세지")
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("메세지 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	@Comment("채팅방 아이디")
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	@Comment("발신 회원 아이디")
	private Member member;

	@Comment("내용")
	private String content;

	@Comment("작성 시간")
	private LocalDateTime createdAt;

	@Builder
	public ChatMessage(ChatRoom chatRoom, Member member, String content, LocalDateTime createdAt) {
		this.chatRoom = chatRoom;
		this.member = member;
		this.content = content;
		this.createdAt = createdAt;
	}
}
