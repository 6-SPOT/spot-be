package spot.spot.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NearByWorkersResponse(
    @Schema(description = "해결사 아이디", example = "1")
    long id,
    @Schema(description = "해결사 이름", example = "고경훈")
    String name,
    @Schema(description = "해결사 프로필 이미지", example = "https://~~~.png")
    String profile_img,
    @Schema(description = "해결사 경도", example = "133.215241214")
    double lat,
    @Schema(description = "해결사 위도", example = "123.232452422")
    double lng
) {

}
