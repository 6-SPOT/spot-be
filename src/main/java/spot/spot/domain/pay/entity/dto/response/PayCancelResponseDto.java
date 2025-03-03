package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;

@Builder
public record PayCancelResponseDto(
        String nickname,
        String domain,
        int amount,
        int cancelAmount
) {

    public static PayCancelResponseDto of(PayCancelResponse payCancelResponse) {
        return PayCancelResponseDto.builder()
                .nickname(payCancelResponse.getPartner_user_id())
                .domain(payCancelResponse.getPartner_order_id())
                .amount(payCancelResponse.getAmount().getTotal())
                .cancelAmount(payCancelResponse.getCanceled_amount().getTotal())
                .build();
    }
}
