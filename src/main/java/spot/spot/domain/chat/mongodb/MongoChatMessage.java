package spot.spot.domain.chat.mongodb;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.member.entity.Member;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat_messages")
public class MongoChatMessage {

	@Id
	private String id;
	private Long chatRoomId;
	private Long senderId;
	private String senderNickName;
	private String content;
	private LocalDateTime createdAt;
}
