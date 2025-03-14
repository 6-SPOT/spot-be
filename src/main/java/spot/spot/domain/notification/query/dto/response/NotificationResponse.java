package spot.spot.domain.notification.query.dto.response;

import io.netty.channel.ChannelHandler.Sharable;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponse(
    @Schema(description = "알림 아이디", example = "1")
    long id,
    @Schema(description = "생성 시간", example = "yyyy-mm-dd hh:mm:ss")
    LocalDateTime created_at,
    @Schema(description = "알림의 내용", example = "명철님이 일을 완료하였습니다.")
    String content,
    @Schema(description = "보낸 이의 아이디")
    long sender_id,
    @Schema(description = "보낸 이의 이름")
    String sender_name,
    @Schema(description = "보낸 이의 프로필 사진")
    String sender_img,
    @Schema(description = "메시지의 타입")
    String msg_type
) {}
