package spot.spot.domain.job.v1.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.v1.query.controller._docs.WorkerQueryDocsV1;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.v1.query.service.WorkerQueryServiceV1;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job/v1")
public class WorkerQueryControllerV1 implements WorkerQueryDocsV1 {

    private final WorkerQueryServiceV1 workerQueryServiceV1;

    @GetMapping("/search/jpql")
    public Slice<NearByJobResponse> nearByJobWithJPQL(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return workerQueryServiceV1.getNearByJobListWithJPQL(lat, lng, zoom, pageable);
    }

    @GetMapping("/search/native-query")
    public Slice<NearByJobResponse> nearByJobWtihNativeQuery(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return workerQueryServiceV1.getNearByJobListWithNativeQuery(lat, lng, zoom, pageable);
    }

    @GetMapping("/search/query-dsl")
    public Slice<NearByJobResponse> nearByJobWithQueryDsl(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return workerQueryServiceV1.getNearByJobListWithQueryDsl(lat, lng, zoom, pageable);
    }
}
