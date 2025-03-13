package spot.spot.global.healthcheck.query.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import spot.spot.global.healthcheck.command.dto.SampleDto;
import spot.spot.global.logging.Logging;

@Tag(name = "다. HEALTH CHECK QUERY API")
public interface HealthCheckQueryDocs {
    // 예외 발생 테스트 (정의된 예외)
    @GetMapping
    @Operation(summary = "커스텀 에러 출력 확인")
    public void throwGlobalException();
    // 예기치 않은 예외 테스트 (ArithmeticException)
    @GetMapping
    @Operation(summary = "예측 불가 에러 출력 확인")
    public void throwUnexpectedError();
    //  ResponseEntity<T> 직접 반환 (ResponseBodyAdvice가 적용되지 않아야 함)
    @Logging
    @Operation(summary = "GET METHOD 반환 확인")
    @GetMapping("/ok")
    public ResponseEntity<SampleDto> getResponseEntity();
}
