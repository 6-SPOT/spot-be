package spot.spot.domain.job.query.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.query.controller._docs.WorkerQueryDocs;
import spot.spot.domain.job.query.dto.response.CertificationImgResponse;
import spot.spot.domain.job.query.dto.response.JobDetailResponse;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.service.WorkerQueryService;

@Slf4j
@RestController
@RequestMapping("/api/job/worker")
@RequiredArgsConstructor
public class WorkerQueryController implements WorkerQueryDocs {

    private final WorkerQueryService workerQueryService;

    @GetMapping(value = "/search")
    public Slice<NearByJobResponse> nearByJobs(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return workerQueryService.getNearByJobList(lat, lng, zoom, pageable);
    }

    @GetMapping(value = "/get")
    public JobDetailResponse getOneJob(@RequestParam  long id) {
        return workerQueryService.getOneJob(id);
    }


    @GetMapping("/dash-board")
    public List<JobSituationResponse> getMyJobSituations() {
        return workerQueryService.getMyJobSituations();
    }

    @GetMapping("/certificate")
    public List<CertificationImgResponse> getWorkersCertificationImgList(
        @RequestParam long jobId,
        @RequestParam long workerId
    ) {
        return workerQueryService.getWorkersCertificationImgList(jobId, workerId);
    }
}
