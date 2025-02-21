package spot.spot.domain.job._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;

@Tag(name = "Job4ClientDocs", description = "일 의뢰자를 위한 API 모음")
public interface Job4ClientDocs {

    @Operation(summary = "시킬 일 서버에 등록하기",
    description = """
        일의 메타데이터와 썸네일 사진을 올려주세요. Contnet-Type은 multipart/form-data로 해주십쇼,
        """,
    responses = {
        @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
        content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = """
                (message : "잘못된 형식의 파일입니다. jpg, png, gif, jpeg 중 하나를 선택해주세요")

                """, content = @Content),
        @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
    })
    public void registerJob(
        @RequestPart(value = "request") RegisterJobRequest request,
        @RequestPart(value = "file", required = false ) MultipartFile file
    );

    @Operation(summary = "근처 해결사 찾기",
        description = """
        해결사를 찾기 위한 지도 화면을 위한 API 입니다. 
        페이지가 처음 랜더링 될 떄, map.center()를 통해 중앙 위치를 보내 주세요.
        이후 Draging과 zoomLevel이 바뀔 때마다 API 요청을 호출해주세요.
        
        허용 줌레벨 (
                        case 21 -> 0.05km;
                        case 20 -> 0.1km;
                        case 19 -> 0.2km;
                        case 18 -> 0.5km;
                        case 17 -> 1km;
                        case 16 -> 2km;
                        case 15 -> 5km;
                        case 14 -> 10km;
                        case 13 -> 20km;
                        case 12 -> 50km;
                        default -> 100km;
        
        )
         
        현재는 단순 API 호출이지만, 이후 최적화로 성능을 높이겠습니다. 
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
        })
    @GetMapping("/near-by")
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = true, defaultValue = "21") int zoom
    );

}
