package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;

@Builder
public record PayOrderResponseDto (
        String nickname,
        String domain,
        int amount,
        String content
){

    public static PayOrderResponseDto of(PayOrderResponse payOrderResponse) {
        return PayOrderResponseDto.builder()
                .nickname(payOrderResponse.getPartner_user_id())
                .domain(payOrderResponse.getPartner_order_id())
                .amount(payOrderResponse.getAmount().getTotal())
                .content(payOrderResponse.getItem_name())
                .build();
    }
}
