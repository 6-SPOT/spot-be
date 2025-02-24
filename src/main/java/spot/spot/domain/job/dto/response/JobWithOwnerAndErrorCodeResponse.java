package spot.spot.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import spot.spot.domain.job.entity.Job;
import spot.spot.global.response.format.ErrorCode;

@Builder
public record JobWithOwnerAndErrorCodeResponse(
    @Schema(description = "일 객체")
    Job job,
    @Schema(description = "일의 주인")
    long ownerId,
    @Schema(description = "만약 쿼리문이 NULL 뱉었다면 어떤 오류인지 확인")
    ErrorCode errorcode
) {

}
