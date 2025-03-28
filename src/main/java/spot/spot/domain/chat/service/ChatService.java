package spot.spot.domain.chat.service;

import java.util.List;

import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;
import spot.spot.domain.chat.dto.request.ChatRoomCreateRequest;
import spot.spot.domain.chat.dto.response.ChatListResponse;
import spot.spot.domain.chat.dto.response.ChatMessageResponse;

public interface ChatService {
	ChatMessageResponse saveMessage(Long roomId, ChatMessageCreateRequest chatMessageDto, Long memberId);

	// 이전 메시지 가져오기
	List<ChatMessageResponse> getChatHistory(Long roomId, Long memberId);

	// 메시지 읽음 처리
	void messageRead(Long roomId, Long memberId);

	public List<ChatListResponse> getMyChatRooms(Long memberId);

	Long getOrCreateChatRoom(ChatRoomCreateRequest chatRoomCreateRequest, Long memberId);

}
