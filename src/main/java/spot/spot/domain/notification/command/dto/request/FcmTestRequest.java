package spot.spot.domain.notification.command.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record FcmTestRequest (
    @Schema(description = "받는 사람 아이디", example = "2")
    Long receiver_id,
    @Schema(description = "내용물", example = "하이루 ㅋ")
    String content
) {

}
