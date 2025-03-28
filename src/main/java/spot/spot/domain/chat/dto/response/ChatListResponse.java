package spot.spot.domain.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatListResponse(
	Long roomId,
	String title,
	Long unReadCount) {
}
