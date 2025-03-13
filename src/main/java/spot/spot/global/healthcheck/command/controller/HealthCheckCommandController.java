package spot.spot.global.healthcheck.command.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.global.healthcheck.command.controller._docs.HealthCheckCommandDocs;
import spot.spot.global.healthcheck.command.dto.SampleDto;
import spot.spot.global.logging.Logging;

@RestController
@RequestMapping("/api/health")
public class HealthCheckCommandController implements HealthCheckCommandDocs {

    @Logging
    @PostMapping("/post-dto")
    public SampleDto postDto(@RequestBody SampleDto dto) {
        return dto;
    }
}
