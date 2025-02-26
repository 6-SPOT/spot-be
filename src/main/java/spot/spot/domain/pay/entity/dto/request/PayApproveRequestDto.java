package spot.spot.domain.pay.entity.dto.request;

public record PayApproveRequestDto(
        String pgToken,
        int totalAmount,
        String tid
) {
}