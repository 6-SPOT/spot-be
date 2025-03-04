package spot.spot.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.domain.job.entity.MatchingStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobSituationResponse {
    @Schema(description = "일 아이디")
    private long jobId;
    @Schema(description = "일 제목")
    private String title;
    @Schema(description = "일의 이미지")
    private String img;
    @Schema(description = "일의 내용")
    private String content;
    @Schema(description = "일의 현황")
    private MatchingStatus status;
    @Schema(description = "해결사 아이디")
    private long memberId;
    @Schema(description = "해결사 이름")
    private String nickName;
    @Schema(description = "해결사 전화번호")
    private String phone;
}
