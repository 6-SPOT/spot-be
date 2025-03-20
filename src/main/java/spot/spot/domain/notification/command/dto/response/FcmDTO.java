package spot.spot.domain.notification.command.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FcmDTO(
    @Schema(description = "알림의 제목", example = "박제가 되어버린 천재를 아시오?")
    String title,
    @Schema(description = "알림 본문", example = "직렬화 해서 안의 구조 꾸며도 괜춘!")
    String body) {

    public static FcmDTO create(String title, String body) {
        return FcmDTO.builder().title(title).body(body).build();
    }
}
