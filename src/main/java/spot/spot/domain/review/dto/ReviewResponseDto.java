package spot.spot.domain.review.dto;

import lombok.Getter;
import spot.spot.domain.review.entity.Review;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {
    private Long id;
    private Long targetId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.targetId = review.getTargetId();
        this.score = review.getScore();
        this.comment = review.getComment();
    }
}

