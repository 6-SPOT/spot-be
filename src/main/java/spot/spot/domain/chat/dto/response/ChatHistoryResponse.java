package spot.spot.domain.chat.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record ChatHistoryResponse(
	Long currentMemberId,
	List<ChatMessageResponse> messages
) {
}
