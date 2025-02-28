package spot.spot.domain.pay.entity.dto.request;

public record PayApproveRequestDto(
        String pgToken,
        String content,
        int totalAmount,
        String tid
) {
}