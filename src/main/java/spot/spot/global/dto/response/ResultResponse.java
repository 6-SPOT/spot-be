package spot.spot.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ResultResponse<T> {
    private String status;
    private String message;
    private T data;

    public static<T> ResultResponse<T> success (T data) {
        return new ResultResponse<>("success", "정상적으로 처리하였습니다.", data);
    }

    public static <T> ResultResponse<T> fail (String message) {
        return new ResultResponse<>("fail", message, null);
    }
}
