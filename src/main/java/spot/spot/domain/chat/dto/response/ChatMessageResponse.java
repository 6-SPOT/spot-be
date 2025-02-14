package spot.spot.domain.chat.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChatMessageResponse(
	@NotBlank String senderNickname,
	String content
) {
}
