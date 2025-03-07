package spot.spot.domain.pay.entity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import spot.spot.domain.pay.entity.PayHistory;
import spot.spot.domain.pay.entity.dto.request.PayReadyRequestDto;

@Builder
public record PayReadyResponseDto(
        @Schema(description = "카카오페이 결제 PC url", example = "https://online-payment.kakaopay.com/mockup/bridge/pc/pg/one-time/payment/b453bfd8ca4241e56d4740b76f53f2f28cc036d4db83effbccd758e4a7e58d67")
        String redirectPCUrl,
        @Schema(description = "카카오페이 결제 Mobile url", example = "https://online-payment.kakaopay.com/mockup/bridge/mobile-web/pg/one-time/payment/279935155306a3f71f3b5019d78c5d90b009bebd85bd2493023a8ed73de0826c")
        String redirectMobileUrl,
        @Schema(description = "결제 상품 id", example = "T1231ADFV2424")
        String tid

) {

        public static PayReadyResponseDto of(PayReadyResponse payReadyResponse) {
                return  PayReadyResponseDto.builder()
                        .redirectPCUrl(payReadyResponse.getNext_redirect_pc_url())
                        .redirectMobileUrl(payReadyResponse.getNext_redirect_mobile_url())
                        .tid(payReadyResponse.getTid())
                        .build();
        }

        public static PayReadyResponseDto create(String redirectPcUrl, String redirectMobileUrl, String tid, PayHistory payHistory) {
                return PayReadyResponseDto.builder()
                        .redirectPCUrl(redirectPcUrl)
                        .redirectMobileUrl(redirectMobileUrl)
                        .tid(tid)
                        .build();
        }
}
