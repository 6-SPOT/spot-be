package spot.spot.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버가 존재하지 않습니다."),
    NOT_ALLOW_STRING(HttpStatus.INTERNAL_SERVER_ERROR, "백엔드 담당자가 String으로 반환을 설정했습니다. String 반환은 허용되지 않습니다. 담당자에게 문의하세요!")
    ;
    private final HttpStatus status;
    private final String message;
    // global (공통)
}
