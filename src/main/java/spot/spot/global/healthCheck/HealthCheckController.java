package spot.spot.global.healthCheck;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import spot.spot.global.logging.ColorLogger;
import spot.spot.global.logging.Logging;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    // 예외 발생 테스트 (정의된 예외)
    @GetMapping("/exception")
    public void throwGlobalException() {
        throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
    }

    // 예기치 않은 예외 테스트 (ArithmeticException)
    @GetMapping("/unexpected-error")
    public void throwUnexpectedError() {
        int result = 10 / 0; // ArithmeticException 발생
    }

    //  ResponseEntity<T> 직접 반환 (ResponseBodyAdvice가 적용되지 않아야 함)
    @GetMapping("/response-entity")
    public ResponseEntity<SampleDto> getResponseEntity() {
        return ResponseEntity.ok(new SampleDto(2, "ResponseEntity 사용"));
    }

    // POST 요청으로 DTO 테스트
    @Logging
    @PostMapping("/post-dto")
    public SampleDto postDto(@RequestBody SampleDto dto) {
        return dto; // 공통 응답으로 감싸져야 함
    }
}

// ✅ DTO 클래스
@Data
@AllArgsConstructor
class SampleDto {
    private int id;
    private String name;
}
