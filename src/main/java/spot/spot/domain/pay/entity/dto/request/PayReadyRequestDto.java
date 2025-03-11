package spot.spot.domain.pay.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record PayReadyRequestDto(

        @NotBlank(message = "일 타이틀이 비어있습니다. 확인해주세요.")
        String content,

        @Positive(message = "가격은 0보다 작거나 비어있을 수 없습니다.")
        int amount,

        @PositiveOrZero(message = "포인트는 음수일 수 없습니다.")
        int point,

        @NotNull(message = "잡 아이디 값은 필수입니다.")
        Long jobId

) {
    public static PayReadyRequestDto create(String title, int amount, int point, Long jobId) {
        return PayReadyRequestDto.builder()
                .content(title)
                .amount(amount)
                .point(point)
                .jobId(jobId)
                .build();
    }
}
