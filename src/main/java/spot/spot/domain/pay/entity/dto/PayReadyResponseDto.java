package spot.spot.domain.pay.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PayReadyResponseDto(
        @Schema(description = "카카오페이 상품 ID", example = "T7b676f5033875bdaf3a")
        String tid,
        @Schema(description = "카카오페이 결제 url", example = "https://online-payment.kakaopay.com/mockup/bridge/pc/pg/one-time/payment/b453bfd8ca4241e56d4740b76f53f2f28cc036d4db83effbccd758e4a7e58d67")
        String redirectUrl
) {
}
