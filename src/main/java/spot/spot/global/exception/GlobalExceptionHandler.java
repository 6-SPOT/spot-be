package spot.spot.global.exception;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spot.spot.global.dto.response.ResultResponse;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final HttpHeaders jsonHeaders;

    static {
        jsonHeaders = new HttpHeaders();
        jsonHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
    }
    // 사용자가 예측 가능한 에러 발생 시
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ResultResponse<Object>> handleGlobalException ( GlobalException globalException) {
        return ResponseEntity
            .status(globalException.getErrorCode().getStatus())
            .headers(jsonHeaders)
            .body(ResultResponse.fail(globalException.getErrorCode().getMessage()));
    }

    // 예기치 못한 에러 발생 시 (일단 에러 내용이 front 한테도 보이게 뒀습니다. 배포할 때 고치겠습니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse<Object>> handleUnExpectException (Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .headers(jsonHeaders)
            .body(ResultResponse.fail("서버 내부 오류 발생: " + e.getMessage()));
    }

}
