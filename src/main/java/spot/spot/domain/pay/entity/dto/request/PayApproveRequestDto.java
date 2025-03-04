package spot.spot.domain.pay.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record PayApproveRequestDto(

        @NotBlank(message = "pgToken 값이 누락되었습니다.")
        String pgToken,

        @NotBlank(message = "일의 타이틀은 빈 값일 수 없습니다.")
        String content,

        @Positive(message = "가격은 양수 값이여야 합니다.")
        int totalAmount,

        @NotBlank(message = "tid값은 빈 값일 수 없습니다.")
        String tid
) {

        public static PayApproveRequestDto create(String pgToken, String content, int totalAmount, String tid) {
             return PayApproveRequestDto.builder()
                     .pgToken(pgToken)
                     .content(content)
                     .totalAmount(totalAmount)
                     .tid(tid)
                     .build();
        }
}