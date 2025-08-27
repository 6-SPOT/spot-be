package spot.spot.domain.job.command.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ConfirmOrRejectRequest(
    @Schema(description = "해결 완료 시킬 아이디")
    long jobId,
    @Schema(description = "true = YES, false = NO")
    boolean isYes
) {}
