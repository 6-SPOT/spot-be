package spot.spot.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterJobRequest(
    @Schema(description = "일의 제목", example = "음쓰 버려주실 분~")
    String title,
    @Schema(description = "일의 내용", example = "아파트 현관문 비번: 6541")
    String content,
    @Schema(description = "일의 보상금액(단위- 만원)", example = "10000")
    int money,
    @Schema(description = "위도", example = "123.2324225")
    double lat,
    @Schema(description = "경도", example = "245.2242557")
    double lng
) {

}
