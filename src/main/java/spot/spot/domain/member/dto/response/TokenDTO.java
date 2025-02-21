package spot.spot.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TokenDTO (
    @Schema(description = "accessToken", example = "Bearer ~")
    String accessToken
) {}
