package spot.spot.domain.review.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    @NotNull
    private Long jobId;

    @NotNull
    private Long targetId;

    @NotNull
    private Long writerId;

    @NotNull
    @Min(1) @Max(5)
    private Integer score;

    private String comment;
}
