package spot.spot.domain.job.command.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterJobResponse(

        @NotNull(message = "잡 id는 빈 값일 수 없습니다.")
        Long jobId
) {

    public static RegisterJobResponse create(Long jobId) {
        return RegisterJobResponse.builder()
                .jobId(jobId)
                .build();
    }
}
