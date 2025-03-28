package spot.spot.domain.chat.kafka;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import spot.spot.domain.chat.dto.KafkaMessage;
import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;

@Service
@RequiredArgsConstructor
@Profile("kafka")
public class ConsumerService {

	private final SimpMessageSendingOperations messageTemplate;

	@KafkaListener(topics = "chat-topic", groupId = "chat-group-${server.port}")
	public void listen(KafkaMessage kafkaMessage) {
		Long roomId = kafkaMessage.getRoomId();
		String content = kafkaMessage.getContent();
		ChatMessageCreateRequest messageDto = ChatMessageCreateRequest.builder()
			.content(content)
			.build();
		messageTemplate.convertAndSend("/api/topic/" + roomId, messageDto);
	}
}
