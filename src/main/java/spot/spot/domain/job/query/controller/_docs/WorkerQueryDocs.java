package spot.spot.domain.job.query.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spot.spot.domain.job.query.dto.response.CertificationImgResponse;
import spot.spot.domain.job.query.dto.response.JobDetailResponse;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Tag(name = "4. WORKER QUERY API", description = "<br/> 해결사를 위한 조회 API")
public interface WorkerQueryDocs {

    @Operation(summary = "근처 소일거리 찾기",
        description = """
        위도, 경도, 줌 레벨을 보내지 않으면 default 값으로 설정 됨.
        default 값: (위도 경도 -> 사용자의 최근 등록 위치, zoom -> 21)
        """)
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
        """)
    @GetMapping
    public JobDetailResponse getOneJob(
        @RequestParam long id
    );

    @Operation(summary = "내가 신청하거나 진행 중인 일 현황 조회",
        description = "내가 신청하거나 진행 중인 일의 현황을 볼 수 있는 API")
    @GetMapping
    public List<JobSituationResponse> getMyJobSituations();

    @Operation(summary = "해결사의 job_id에 해당하는 일에 대한 인증 사진 리스트",
        description = "해결사의 job_id에 해당하는 일에 대한 인증 사진 리스트을 볼 수 있는 API")
    @GetMapping
    public List<CertificationImgResponse> getWorkersCertificationImgList(
        @RequestParam long jobId,
        @RequestParam long workerId
    );
}
