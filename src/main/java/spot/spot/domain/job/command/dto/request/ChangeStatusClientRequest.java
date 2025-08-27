package spot.spot.domain.job.command.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeStatusClientRequest(
    @Schema(description = "해결사가 맡으면 하는 일의 아이디")
    long jobId,
    @Schema(description = "해결사 아이디")
    long workerId
) {

}
