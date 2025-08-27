package spot.spot.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder

public record AuthoredReview(
    @Schema(description = "리뷰 id입니다.")
    long id,
    @Schema(description = "내가 리뷰를 쓴 사람의 이름 입니다.")
    String receiverName,
    @Schema(description = "내가 리뷰를 쓴 사람의 id입니다.")
    long receiverId,
    @Schema(description = "내가 리뷰를 쓴 사람의 프로필 이미지입니다.")
    String receiverImg,
    @Schema(description = "리뷰의 점수입니다.")
    int score,
    @Schema(description = "리뷰입니다.")
    String comment
) {}
