package spot.spot.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceivedReview {
    @Schema(description = "리뷰 id입니다.")
    private Long id;

    @Schema(description = "나한테 리뷰를 써준 사람의 이름입니다.")
    private String writerNickname;

    @Schema(description = "나한테 리뷰를 써준 사람의 id입니다.")
    private Long writerId;

    @Schema(description = "써준사람의 프로필 이미지입니다.")
    private String writerImg;

    @Schema(description = "리뷰의 점수입니다.")
    private Integer score;

    @Schema(description = "리뷰입니다.")
    private String comment;
}
