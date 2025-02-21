package spot.spot.domain.job._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PutMapping;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;

@Tag(name = "Job4Worker", description = "해결사를 위한 API 모음")
public interface Job4WorkerDocs {

    @Operation(summary = "구직 등록하기",
        description = """
        자신의 프로필과 강점을 입력해주세요. (강점은 Enum 고르기) Contnet-Type은 multipart/form-data로 해주십쇼,
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @PutMapping
    public void registerWorker(RegisterWorkerRequest request);

}
