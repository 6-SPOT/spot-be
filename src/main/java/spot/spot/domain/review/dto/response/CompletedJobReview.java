package spot.spot.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CompletedJobReview {
    @Schema(description = "리뷰 id입니다.")
    private Long id;

    @Schema(description = "나한테 리뷰를 써준 사람의 이름입니다.")
    private String writerNickname;

    @Schema(description = "나한테 리뷰를 써준 사람의 id입니다.")
    private Long writerId;

    @Schema(description = "리뷰의 점수입니다.")
    private Integer score;

    @Schema(description = "리뷰입니다.")
    private String comment;

    public CompletedJobReview(Long id, String writerNickname,
                              Long writerId, Integer score,
                              String comment) {
        this.id = id;
        this.writerNickname = writerNickname;
        this.writerId = writerId;
        this.score = score;
        this.comment = comment;
    }
}
