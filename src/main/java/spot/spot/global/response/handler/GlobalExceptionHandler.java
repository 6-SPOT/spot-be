package spot.spot.global.response.handler;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spot.spot.global.logging.ColorLogger;
import spot.spot.global.response.format.ResultResponse;
import spot.spot.global.response.format.GlobalException;
import org.springframework.validation.FieldError;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Set;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final HttpHeaders jsonHeaders;
    static {
        jsonHeaders = new HttpHeaders();
        jsonHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        String firstErrorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("잘못된 요청입니다.");

        ResultResponse<Object> response = ResultResponse.fail(firstErrorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationListException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("잘못된 요청입니다.");

        ResultResponse<Object> response = ResultResponse.fail(errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders)
                .body(response);
    }

    // 사용자가 예측 가능한 에러 발생 시
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ResultResponse<Object>> handleGlobalException ( GlobalException globalException) {
        log.error("Global Exception: {}",globalException.getMessage());
        return ResponseEntity
            .status(globalException.getErrorCode().getStatus())
            .headers(jsonHeaders)
            .body(ResultResponse.fail(globalException.getErrorCode().getMessage()));
    }
    // 예기치 못한 에러 발생 시 (일단 에러 내용이 front 한테도 보이게 뒀습니다. 배포할 때 고치겠습니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse<Object>> handleUnExpectException (Exception e) {
        log.error("예상치 못한 에러: {}", ExceptionUtils.getStackTrace(e).replace("\n", "\\n").replace("\r", ""));
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .headers(jsonHeaders)
            .body(ResultResponse.fail("서버 내부 오류 발생: " + e.getMessage()));
    }

    @MessageExceptionHandler(MessagingException.class)
    @SendToUser("/errors")
    public String handleMessageException(MessagingException e) {
        ColorLogger.red("STOMP 발송 중 에러 발생! : {} ", e.getMessage());
        return "에러 발생" + e.getMessage();
    }

}
