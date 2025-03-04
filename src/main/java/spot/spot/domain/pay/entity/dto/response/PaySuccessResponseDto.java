package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;

@Builder
public record PaySuccessResponseDto(
        int totalPointAmount
) {

    public static PaySuccessResponseDto of(int totalPointAmount) {
        return PaySuccessResponseDto.builder()
                .totalPointAmount(totalPointAmount)
                .build();
    }
}
