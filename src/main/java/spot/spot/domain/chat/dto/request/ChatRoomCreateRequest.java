package spot.spot.domain.chat.dto.request;

public record ChatRoomCreateRequest(
	Long jobId,
	Long otherMemberId
) {
}
