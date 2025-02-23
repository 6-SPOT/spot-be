package spot.spot.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record Ask2ClientGetANewJobRequest (
    @Schema(description = "해결사가 맡아서 하고 싶은 일의 아이디")
    long jobId)
{ }
