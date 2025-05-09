package spot.spot.domain.chat.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import spot.spot.domain.chat.dto.KafkaMessage;
import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;
import spot.spot.domain.chat.dto.response.ChatMessageResponse;
import spot.spot.domain.chat.service.ChatService;
import spot.spot.domain.chat.service.SimpleChatService;

@Controller
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaStompChatController implements StompChatController{
	private final ChatService chatService;
	private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

	@MessageMapping("/{roomId}") // roomId로 메세지 보내기
	public void sendMessage(@DestinationVariable Long roomId, ChatMessageCreateRequest chatMessageDto, SimpMessageHeaderAccessor headerAccessor) {
		Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
		ChatMessageResponse chatMessageResponse = chatService.saveMessage(roomId, chatMessageDto, memberId);
		KafkaMessage kafkaMessage = new KafkaMessage(roomId, chatMessageDto.content());
		kafkaTemplate.send("chat-topic", kafkaMessage);
	}
}
