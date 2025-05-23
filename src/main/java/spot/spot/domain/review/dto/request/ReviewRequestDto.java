package spot.spot.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReviewRequestDto {
    @NotNull
    private Long jobId;

    @NotNull
    private Long targetId;

    @NotNull
    @Min(1) @Max(5)
    private Integer score;

    private String comment;
}
