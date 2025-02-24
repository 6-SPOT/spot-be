package spot.spot.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChatMessageCreateRequest(
	String content
) {
}
