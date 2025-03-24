package spot.spot.global.healthcheck.query.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import spot.spot.global.healthcheck.command.dto.SampleDto;
import spot.spot.global.healthcheck.query.controller._docs.HealthCheckQueryDocs;
import spot.spot.global.logging.Logging;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthCheckQueryController implements HealthCheckQueryDocs {

    @GetMapping("/exception")
    public void throwGlobalException() {
        throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
    }

    @GetMapping("/unexpected-error")
    public void throwUnexpectedError() {
        int result = 10 / 0;
    }

    @Logging
    @GetMapping("/ok")
    public ResponseEntity<SampleDto> getResponseEntity() {
        return ResponseEntity.ok(new SampleDto(2, "ResponseEntity 사용"));
    }

    @GetMapping("/fake-api/ok")
    public ResponseEntity<String> healthCheckFromFakeApiServer() {
        return ResponseEntity.ok("ok");
    }
}

