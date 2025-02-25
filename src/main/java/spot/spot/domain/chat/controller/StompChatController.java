package spot.spot.domain.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;
import spot.spot.domain.chat.service.ChatService;

@Controller
@RequiredArgsConstructor
public class StompChatController {

	private final SimpMessageSendingOperations messageTemplate;
	private final ChatService chatService;

	@MessageMapping("/api/chat/{roomId}") // roomId로 메세지 보내기
	public void sendMessage(@DestinationVariable Long roomId, ChatMessageCreateRequest chatMessageDto, Authentication authentication) {
		Long memberId = Long.parseLong(authentication.getName());
		chatService.saveMessage(roomId, chatMessageDto, memberId);
		messageTemplate.convertAndSend("/api/topic/" + roomId, chatMessageDto);
	}


}
