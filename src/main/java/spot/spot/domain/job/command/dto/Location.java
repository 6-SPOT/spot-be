package spot.spot.domain.job.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "장소 객체")
public record Location (
    @Schema(description = "위도")
    double lat,
    @Schema(description = "경도")
    double lng
) {}
