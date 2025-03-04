package spot.spot.domain.pay.entity.dto.response;

import lombok.Builder;

@Builder
public record PayApproveResponseDto(
        String nickname,
        String domain,
        String content,
        int amount
) {

    public static PayApproveResponseDto of(PayApproveResponse payApproveResponse) {
        return PayApproveResponseDto.builder()
                .nickname(payApproveResponse.getPartner_user_id())
                .domain(payApproveResponse.getPartner_order_id())
                .content(payApproveResponse.getItem_name())
                .amount(payApproveResponse.getAmount().getTotal())
                .build();
    }

    public static PayApproveResponseDto create(String nickname, String domain, String content, int amount) {
        return PayApproveResponseDto.builder()
                .nickname(nickname)
                .domain(domain)
                .content(content)
                .amount(amount)
                .build();
    }
}
