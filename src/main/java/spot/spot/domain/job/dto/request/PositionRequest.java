package spot.spot.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PositionRequest (
    @Schema(description = "일 아이디")
    long jobId,
    @Schema(description = "위도")
    double lat,
    @Schema(description = "경도")
    double lng
) {}
