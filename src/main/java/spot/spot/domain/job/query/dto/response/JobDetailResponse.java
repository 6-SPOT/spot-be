package spot.spot.domain.job.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.domain.job.command.entity.MatchingStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailResponse{
    @Schema(description = "일의 고유 아이디")
    private long id;
    @Schema(description = "일의 제목")
    private String title;
    @Schema(description = "일의 내용")
    private String content;
    @Schema(description = "일의 프로필 사진")
    private String picture;
    @Schema(description = "위도")
    private double lat;
    @Schema(description = "경도")
    private double lng;
    @Schema(description = "돈")
    private int money;
    @Schema(description = "카카오페이 결제 번호")
    private String tid;
    @Schema(description = "의뢰인 아이디")
    private long clientId;
    @Schema(description = "의뢰인 닉네임")
    private String nickname;
    @Schema(description = "의뢰인 프로필 사진")
    private String clientImg;
    @Schema(description = "요청한 사용자와 일의 관계")
    private MatchingStatus myStatus;
    @Schema(description = "내가 이 일의 주인인가")
    private boolean isOwner;
}
