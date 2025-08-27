package spot.spot.domain.job.v2.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.v2.query.controller._docs.WorkerQueryDocsV2;
import spot.spot.domain.job.v2.query.service.WorkerQueryServiceV2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job/v2")
public class WorkerQueryControllerV2 implements WorkerQueryDocsV2 {

    private final WorkerQueryServiceV2 workerQueryServiceV2;

    @GetMapping("/search/jpql")
    public Slice<NearByJobResponse> nearByJobWithJPQL(Double lat, Double lng, Integer zoom,
        Pageable pageable) {
        return workerQueryServiceV2.getNearByJobListWithJPQL(lat,lng,zoom,pageable);
    }

    @GetMapping("/search/native-query")
    public Slice<NearByJobResponse> nearByJobWtihNativeQuery(Double lat, Double lng, Integer zoom,
        Pageable pageable) {
        return workerQueryServiceV2.getNearByJobListWithNativeQuery(lat,lng,zoom,pageable);
    }

    @GetMapping("/search/query-dsl")
    public Slice<NearByJobResponse> nearByJobWithQueryDsl(Double lat, Double lng, Integer zoom,
        Pageable pageable) {
        return workerQueryServiceV2.getNearByJobListWithQueryDsl(lat, lng, zoom, pageable);
    }
}
