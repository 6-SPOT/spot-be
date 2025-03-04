package spot.spot.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PositionResponse (
    @Schema(description = "일 아이디")
    long jobId,
    @Schema(description = "위도")
    double lat,
    @Schema(description = "경도")
    double lng,
    @Schema(description = "보낸 이 아이디")
    long senderId,
    @Schema(description = "보낸 이 이름")
    String senderName,
    @Schema(description = "보낸 이 프로필 이미지")
    String senderImg,
    @Schema(description = "보낸 시각")
    long sendDateTime
) {}
