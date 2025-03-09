package spot.spot.domain.job._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.dto.request.AttenderResponse;
import spot.spot.domain.job.dto.response.JobSituationResponse;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.dto.response.RegisterJobResponse;

@Tag(
    name = "Job4ClientDocs",
    description = "일 의뢰자를 위한 API 모음")
public interface ClientDocs {

    @Operation(summary = "시킬 일 서버에 등록하기",
    description = """
        일의 메타데이터와 썸네일 사진을 올려주세요. Contnet-Type은 multipart/form-data로 해주십쇼,
        """)
    @PutMapping
    public RegisterJobResponse registerJob(
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
        """)
    @GetMapping("/near-by")
    public List<NearByWorkersResponse> nearByWorkersResponseList (
        @RequestParam(required = true) double lat,
        @RequestParam(required = true) double lng,
        @RequestParam(required = true, defaultValue = "21") Integer zoom
    );

    @Operation(summary = "일 신청자 리스트 얻기",
        description = """
        특정 job_id의 일을 신청한 신청자의 리스트를 얻습니다.
        """)
    @GetMapping
    public Slice<AttenderResponse> getAttenderList (
        @RequestParam long id,
        Pageable pageable
    );

    @Operation(summary = "해결사에 일 의뢰하기",
        description = """
        특정 job_id의 일을 해결사에게 의뢰합니다.
        """)
    @PostMapping
    public void askJob2Worker (
        @RequestBody ChangeStatusClientRequest request
    );

    @Operation(summary = "너 일해라",
        description = """
        일을 하겠다고 자원한 해결사 중 한명의 요청을 승낙하기 - 너 일해라
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = " 정상 응답",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "그런 해결사가 존재하지 않습니다.")
                """, content = @Content),
        })
    @PostMapping
    public void acceptJobRequestOfWorker (
        @RequestBody YesOrNoWorkersRequest request
    );


    @Operation(summary = "일 철회 요청",
        description = """
        잠수탄 놈에 대해 일 철회 요청 (SLEEP 처리 -> 10분 후 CANCEL로 조절)
        """)
    @PostMapping
    public void requestWithdrawal (
        @RequestBody ChangeStatusClientRequest request
    );

    @Operation(summary = "내가 맡긴 일의 현황 보기",
        description = """
        
        """)
    @GetMapping
    public List<JobSituationResponse> getSituationByOwner();

    @Operation(summary = "해결사의 일 완료 요청을 반려 시키거나 확정",
        description = """
           해결사의 일 완료 요청을 반려 하거나 해결을 확정하는 API
        """)
    @PatchMapping
    public void confirmOrRejectJob(YesOrNoWorkersRequest request);
}
