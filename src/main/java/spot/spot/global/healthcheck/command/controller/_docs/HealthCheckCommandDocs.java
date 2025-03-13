package spot.spot.global.healthcheck.command.controller._docs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import spot.spot.global.healthcheck.command.dto.SampleDto;

@Tag(name= "나. HEALTHCHECK COMMAND API", description = "<br/> Post Method Health Checking ")
public interface HealthCheckCommandDocs {
    @Operation(summary = "POST METHOD 반환 상태 확인")
    public SampleDto postDto(@RequestBody SampleDto dto);
}
