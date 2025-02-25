package spot.spot.domain.job._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import spot.spot.domain.job.dto.request.Job2WorkerRequest;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.request.YesOrNo2ClientsRequest;
import spot.spot.domain.job.dto.response.NearByJobResponse;

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


    @Operation(summary = "근처 소일거리 찾기",
        description = """
        위도, 경도, 줌 레벨을 보내지 않으면 default 값으로 설정 됨.
        위도, 경도 -> 해결사가 최초 등록한 위치
        줌 레벨 -> 21
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = """
        [
            { "message": "Success",
             	"data": [
                     		{
                     			"id" : 1,
                     			"title": "음쓰 버리기",
                     			"content": "음쓰 좀 버려주소~",
                     			"picture": "dspfkpk3kpdofkfkf;dsfkd",
                     			"lat": 124.14151555,
                     			"lng": 253.295252652
                     			"money": 30000,
                     			"dist": 1,
                     			"tid" : 1235
                     		},
                     		{
                     			"id" : 2,
                     			"title": "음쓰 버리기",
                     			"content": "음쓰 좀 버려주소~",
                     			"picture": "dspfkpk3kpdofkfkf;dsfkd",
                     			"lat": 124.14151555,
                     			"lng": 253.295252652
                     			"money": 30000,
                     			"dist": 1,
                     			"tid" : 1235
                     		},
                     		{
                     			"id" : 3,
                     			"title": "음쓰 버리기",
                     			"content": "음쓰 좀 버려주소~",
                     			"picture": "dspfkpk3kpdofkfkf;dsfkd",
                     			"lat": 124.14151555,
                     			"lng": 253.295252652
                     			"money": 30000,
                     			"dist": 1,
                     			"tid" : 1235
                     		},
                     	],
                     	        "pageable": {
                                          "pageNumber": 0,
                                          "pageSize": 50,
                                          "sort": [],
                                          "offset": 0,
                                          "paged": true,
                                          "unpaged": false
                                      },
                                      "size": 50,
                                      "number": 0,
                                      "sort": [],
                                      "first": true,
                                      "last": false,
                                      "numberOfElements": 50,
                                      "empty": false
             }
        ]
        """,
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = NearByJobResponse.class)))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @GetMapping
    public Slice<NearByJobResponse> nearByJobs(
        @RequestParam Double lat,
        @RequestParam Double lng,
        @RequestParam Integer zoom,
        Pageable pageable
    );

    @Operation(summary = "일 하나 상세 확인",
        description = """
        일 하나에 대한 메타 데이터 제공
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @GetMapping
    public NearByJobResponse getOneJob(
        @RequestParam long id
    );

    @Operation(summary = "일을 자신이 하겠다고 일 올린 사람한테 요청하기",
        description = """
        일 요청
        요청이 성공되면 매칭 테이블의 새로운 교차 레코드가 생기고, STATUS가 ATTENDER로 생성됩니다.
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @PostMapping
    public void askingJob2Client(@RequestBody Job2WorkerRequest request);

    @Operation(summary = "일을 시작하기",
        description = """
        MatchingStatus가 YES인 일의 상태를 START로 바꿉니다.
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @PostMapping
    public void startJob(@RequestBody Job2WorkerRequest request);

    @Operation(summary = "의뢰인이 요청한 일 수락 혹은 거절",
        description = """
        의뢰인이 요청한 일에 대해 YES or NO를 설정합니다.
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "(message : \"Success\")",
                content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = """
                (message : "의뢰자가 존재하지 않습니다.")
                """, content = @Content),
        })
    @PostMapping
    public void acceptJobRequestOfClient (
        @RequestBody YesOrNo2ClientsRequest request
    );


}
