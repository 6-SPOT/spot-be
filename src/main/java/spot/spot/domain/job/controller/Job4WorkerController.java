package spot.spot.domain.job.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job._docs.Job4WorkerDocs;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.service.Job4WorkerService;

@RestController
@RequestMapping("/api/job/worker")
@RequiredArgsConstructor
public class Job4WorkerController implements Job4WorkerDocs {

    private final Job4WorkerService job4WorkerService;

    @PutMapping("/register")
    public void registerWorker(@RequestBody  RegisterWorkerRequest request) {
        job4WorkerService.registeringWorker(request);
    }

    @GetMapping("/search")
    public Slice<NearByJobResponse> nearByJobs(
        @RequestParam(required = false, defaultValue = "37.4003214306809") Double lat,
        @RequestParam(required = false, defaultValue = "127.104876545966") Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return job4WorkerService.getNearByJobList("dsl", lat, lng, zoom, pageable);
    }
}
