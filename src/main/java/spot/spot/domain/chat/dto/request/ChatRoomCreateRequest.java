package spot.spot.domain.chat.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomCreateRequest(
	Long jobId,
	Long otherMemberId
) {
}
