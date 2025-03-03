package spot.spot.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record JobResponse(
    @Schema(description = "위도")
    double lat,
    @Schema(description = "경도")
    double lng,
    @Schema(description = "제목")
    String titie,
    @Schema(description = "내용물")
    String content,
    @Schema(description = "금액")
    int money,
    @Schema(description = "일의 이미지")
    String img,
    @Schema(description = "시작 시간")
    LocalDateTime startedAt
) {}
