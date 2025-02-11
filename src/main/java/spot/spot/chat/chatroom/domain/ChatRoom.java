package spot.spot.chat.chatroom.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chat_rooms")
@Comment("채팅방")
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("채팅방 아이디")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_id", nullable = false)
	@Comment("일 아이디")
	private Job job;

	@Comment("채팅방 제목")
	private String title;


	@Comment("채팅방 썸네일 사진")
	private String thumbnailImageUrl;


	@Comment("업데이트 시간")
	private LocalDateTime updatedAt;
}
