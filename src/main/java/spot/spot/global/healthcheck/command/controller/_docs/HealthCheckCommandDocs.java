package spot.spot.global.healthcheck.command.controller._docs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import spot.spot.global.healthcheck.command.dto.SampleDto;

@Tag(name= "99. HEALTHCHECK COMMAND API")
public interface HealthCheckCommandDocs {
    @Operation(summary = "POST METHOD HEALTH CHECKING")
    public SampleDto postDto(@RequestBody SampleDto dto);
}
