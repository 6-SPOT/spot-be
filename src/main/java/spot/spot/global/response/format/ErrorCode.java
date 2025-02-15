package spot.spot.global.response.format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // GLOBAL
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버가 존재하지 않습니다."),
    NOT_ALLOW_STRING(HttpStatus.INTERNAL_SERVER_ERROR, "백엔드 담당자가 String으로 반환을 설정했습니다. String 반환은 허용되지 않습니다. 담당자에게 문의하세요!"),
    // KLAYTN
    FAIL_CONNECT_KLAYTN_NETWORK(HttpStatus.INTERNAL_SERVER_ERROR, "클라이튼 네트워크와 연결을 실패하였습니다."),
    FAIL_CREATE_CONTRACT(HttpStatus.BAD_REQUEST, "컨트랙트 생성에 실패하였습니다."),
    NOT_ALLOW_FROM_ADDRESS(HttpStatus.NOT_ACCEPTABLE, "가이아 전송 주소가 잘못되었습니다."),
    FIELD_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 필드가 없습니다."),
    EMPTY_RESPONSE(HttpStatus.BAD_REQUEST, "응답값이 비어있습니다."),
    // LOG AOP
    FAILED_TO_ACCESS_VARIABLE(HttpStatus.BAD_REQUEST, "특정 필드 접근에 실패했습니다."),
    LOW_AMOUNT(HttpStatus.BAD_REQUEST, "변환값이 0보다 적습니다.")
    ;
    private final HttpStatus status;
    private final String message;
    // global (공통)
}
