package spot.spot.domain.pay.entity.dto.request;

public record PayApproveRequestDto(
        String pgToken,
        String jobTitle,
        int totalAmount,
        String tid
) {
}