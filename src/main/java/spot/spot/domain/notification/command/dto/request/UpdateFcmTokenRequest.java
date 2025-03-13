package spot.spot.domain.notification.command.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateFcmTokenRequest(
    @Schema(description = "fcm 토큰", example = "zxcvoiuwaerlknsdlkfnklasdf/111asjndkl123dasd")
    String fcmToken
) { }
