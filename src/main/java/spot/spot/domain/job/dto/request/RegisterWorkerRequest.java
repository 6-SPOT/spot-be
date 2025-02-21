package spot.spot.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import spot.spot.domain.member.entity.AbilityType;

public record RegisterWorkerRequest (
    @Schema(description = "위도")
    double lat,
    @Schema(description = "경도")
    double lng,
    @Schema(description = "내용")
    String content,
    @Schema(description = "강점 - 강점 종류: API 명세서 확인해주세요 ^~^")
    List<AbilityType> strong
) { }
