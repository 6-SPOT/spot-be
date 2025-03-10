package spot.spot.domain.chat.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;
import spot.spot.global.kafka.KafkaMessage;

// @Service
// @RequiredArgsConstructor
// public class ConsumerService {
//
// 	private final SimpMessageSendingOperations messageTemplate;
//
// 	@KafkaListener(topics = "chat-topic", groupId = "chat-group-${server.port}")
// 	public void listen(KafkaMessage kafkaMessage) {
// 		System.out.println("Received message: " + kafkaMessage);
// 		Long roomId = kafkaMessage.getRoomId();
// 		String content = kafkaMessage.getContent();
// 		ChatMessageCreateRequest messageDto = ChatMessageCreateRequest.builder()
// 			.content(content)
// 			.build();
// 		messageTemplate.convertAndSend("/api/topic/" + roomId, messageDto);
//
// 	}
// }
