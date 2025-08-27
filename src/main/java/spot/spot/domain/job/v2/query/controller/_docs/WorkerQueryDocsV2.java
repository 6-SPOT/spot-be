package spot.spot.domain.job.v2.query.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.RequestParam;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Tag(name = "다. (V2) Worker QUERY API", description = "<br/> 해결사 R API SQL 방언 없이 순수 쿼리만 최적화")
public interface WorkerQueryDocsV2 {

    @Operation(summary = "JPQL로 DB 쿼리로 거리 계산 후 리스트 출력 (default 위도 경도 현 사용자의 위도 경도)")
    public Slice<NearByJobResponse> nearByJobWithJPQL (
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable);

    @Operation(summary = "native로 DB 거리 계산 후 리스트 출력 (default 위도 경도 현 사용자의 위도 경도)")
    public Slice<NearByJobResponse> nearByJobWtihNativeQuery (
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable);

    @Operation(summary = "queyDsl로 DB 거리 계산 후 리스트 출력 (default 위도 경도 현 사용자의 위도 경도)")
    public Slice<NearByJobResponse> nearByJobWithQueryDsl (
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable);


}
