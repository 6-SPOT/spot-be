package spot.spot.global.response.format;

import com.google.api.Http;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // GLOBAL
    NOT_ALLOW_STRING(HttpStatus.INTERNAL_SERVER_ERROR, "백엔드 담당자가 String으로 반환을 설정했습니다. String 반환은 허용되지 않습니다. 담당자에게 문의하세요!"),
    NOT_ALLOW_STATUS_SETTER_4_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Status Setter는 오직 정 응답 코드를 위해서만 쓸 수 있습니다."),
    // SECURITY
    NOT_FOUND_JWT(HttpStatus.UNAUTHORIZED, "JWT가 없습니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "잘못된 JWT 입니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "지원하지 않는 버전의 JWT 입니다."),
    ILLEGAL_JWT(HttpStatus.UNAUTHORIZED, " 잘못된 JWT 입니다."),
    UNKNOWN_JWT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알려지지 않은 JWT 에러 입니다."),
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
    WORKER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버는 해결사로 등록하지 않았습니다."),
    // JOB
    FAILED_2_UPDATE_JOB_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "상태 변경에 실패했습니다."),
    DIDNT_PASS_VALIDATION(HttpStatus.BAD_REQUEST, "요청이 유효성 검증을 통과하지 못했습니다. (1. 구직자 등록 x, 2. 매칭 상태가 현재 요청을 이룰 수 없음, 3. 이미 해당 일을 진행하는 사람임"),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "찾으시는 일은 존재하지 않습니다."),
    MATCHING_NOT_FOUND(HttpStatus.NOT_FOUND, "찾으시는 매칭이 존재하지 않습니다."),
    JOB_IS_ALREADY_STARTED(HttpStatus.NOT_FOUND, "신청 하시려는 일은 이미 시작되었습니다."),
    INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "지도 축소가 너무 과합니다. (◞‸ ◟)"),
    FAIL_PAY_READY(HttpStatus.BAD_GATEWAY, "카카오페이 API 요청이 실패하였습니다."),
    EMPTY_POINT(HttpStatus.NOT_FOUND, "포인트가 모두 소멸되었습니다."),
    PAY_SUCCESS_NOT_FOUND(HttpStatus.NOT_FOUND, "일 등록시 결제된 내역이 없습니다."),
    INVALID_SEARCH_METHOD(HttpStatus.BAD_REQUEST, "요청하신 이름의 구현 매소드를 찾을 수 없습니다."),
    // ALRAM
    INVALID_FCM_TOKEN(HttpStatus.NOT_ACCEPTABLE, "❌ 이 회원은 FCM 토큰이 전무하네요! 오래 접속하지 않았거나, 탈퇴회원 입니다. ❌ "),
    INVALID_TITLE(HttpStatus.NOT_ACCEPTABLE, "❌ 조회하신 일이 존재하지않습니다.! 결제준비된 상품인지 확인해주세요 ❌ "),
    // POINT
    ALREADY_PAY_FAIL(HttpStatus.BAD_REQUEST, "이미 결제 취소된 내역입니다."),
    EMPTY_MEMBER(HttpStatus.BAD_REQUEST, "멤버 값이 비어있습니다."),
    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "일 타이틀 값이 비어있습니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "비용이 0보다 작습니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "포인트 생성 시 포인트는 0보다 커야합니다."),
    INVALID_POINT_COUNT(HttpStatus.BAD_REQUEST, "포인트 생성 시 포인트 개수는 0보다 커야합니다."),
    EMPTY_PG_TOKEN(HttpStatus.BAD_REQUEST, "pgToken 값이 비어있습니다."),
    EMPTY_TID(HttpStatus.BAD_REQUEST, "tid 값이 비어있습니다."),
    EMPTY_POINT_NAME(HttpStatus.BAD_REQUEST, "포인트 이름 값이 비어있습니다."),
    INVALID_POINT_CODE(HttpStatus.BAD_REQUEST, "포인트 코드를 확인해주세요."),
    INVALID_POINT_NAME(HttpStatus.BAD_REQUEST, "포인트 이름을 확인해주세요."),
    FAIL_LOGIN(HttpStatus.BAD_REQUEST, "로그인에 실패하였습니다.")
    ;
    private final HttpStatus status;
    private final String message;
    // global (공통)
}
