package spot.spot.domain.pay.entity.dto;

public record PayApproveRequestDto(
        String pgToken,
        String jobTitle,
        int totalAmount
) {
}