package spot.spot.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import spot.spot.domain.job.entity.Job;

@Builder
public record JobWithOwnerReponse(
    @Schema(description = "일 객체")
    Job job,
    @Schema(description = "일의 주인")
    long ownerId
) { }
