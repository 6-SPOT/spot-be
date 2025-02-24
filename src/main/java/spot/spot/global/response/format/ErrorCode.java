package spot.spot.global.response.format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // GLOBAL
    NOT_ALLOW_STRING(HttpStatus.INTERNAL_SERVER_ERROR, "백엔드 담당자가 String으로 반환을 설정했습니다. String 반환은 허용되지 않습니다. 담당자에게 문의하세요!"),
    // KLAYTN
    FAIL_CONNECT_KLAYTN_NETWORK(HttpStatus.INTERNAL_SERVER_ERROR, "클라이튼 네트워크와 연결을 실패하였습니다."),
    FAIL_CREATE_CONTRACT(HttpStatus.BAD_REQUEST, "컨트랙트 생성에 실패하였습니다."),
    NOT_ALLOW_FROM_ADDRESS(HttpStatus.NOT_ACCEPTABLE, "가이아 전송 주소가 잘못되었습니다."),
    FIELD_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 필드가 없습니다."),
    EMPTY_RESPONSE(HttpStatus.BAD_REQUEST, "응답값이 비어있습니다."),
    // LOG AOP
    FAILED_TO_ACCESS_VARIABLE(HttpStatus.BAD_REQUEST, "특정 필드 접근에 실패했습니다."),
    LOW_AMOUNT(HttpStatus.BAD_REQUEST, "변환값이 0보다 적습니다."),
    // AWS S3
    S3_SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 서버 내부 오류가 있습니다. 담당자 문의 바람"),
    S3_INPUT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 Input에 실패하였습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "지우려는 파일이 S3 내부에 없습니다."),
    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버가 존재하지 않습니다."),
    ITS_NOT_DEFINED_ABILITY(HttpStatus.BAD_REQUEST, "유효하지 않은 WORKER의 능력 입니다."),
    // JOB
    FAILED_2_UPDATE_JOB_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "상태 변경에 실패했습니다."),
    DIDNT_PASS_VALIDATION(HttpStatus.BAD_REQUEST, "요청이 유효성 검증을 통과하지 못했습니다. (1. 구직자 등록 x, 2. 매칭 상태가 현재 요청을 이룰 수 없음, 3. 이미 해당 일을 진행하는 사람임"),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "찾으시는 일은 존재하지 않습니다."),
    JOB_IS_ALREADY_STARTED(HttpStatus.NOT_FOUND, "신청 하시려는 일은 이미 시작되었습니다."),
    INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "지도 축소가 너무 과합니다. (◞‸ ◟)"),
    FAIL_PAY_READY(HttpStatus.BAD_GATEWAY, "카카오페이 API 요청이 실패하였습니다."),
    EMPTY_POINT(HttpStatus.NOT_FOUND, "포인트가 모두 소멸되었습니다."),
    PAY_SUCCESS_NOT_FOUND(HttpStatus.NOT_FOUND, "일 등록시 결제된 내역이 없습니다."),
    INVALID_SEARCH_METHOD(HttpStatus.BAD_REQUEST, "요청하신 이름의 구현 매소드를 찾을 수 없습니다."),
    // ALRAM
    INVALID_FCM_TOKEN(HttpStatus.NOT_ACCEPTABLE, "❌ 이 회원은 FCM 토큰이 전무하네요! 오래 접속하지 않았거나, 탈퇴회원 입니다. ❌ "),
    INVALID_TITLE(HttpStatus.NOT_ACCEPTABLE, "❌ 조회하신 일이 존재하지않습니다.! 결제준비된 상품인지 확인해주세요 ❌ ")
    ;
    private final HttpStatus status;
    private final String message;
    // global (공통)
}
