package spot.spot.domain.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;

public interface StompChatController {
	void sendMessage(@DestinationVariable Long roomId, ChatMessageCreateRequest chatMessageDto, SimpMessageHeaderAccessor headerAccessor);
}
