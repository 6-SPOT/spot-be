package spot.spot.domain.job.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job._docs.Job4WorkerDocs;
import spot.spot.domain.job.dto.request.Ask2ClientGetANewJobRequest;
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

    @GetMapping(value = "/search")
    public Slice<NearByJobResponse> nearByJobs(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return job4WorkerService.getNearByJobList("dsl", lat, lng, zoom, pageable);
    }

    @GetMapping(value = "/get")
    public NearByJobResponse getOneJob(@RequestParam  long id) {
        return job4WorkerService.getOneJob(id);
    }

    @PostMapping("/request")
    public void ask2ClientAboutGettingAnewJob(@RequestBody Ask2ClientGetANewJobRequest request) {
        job4WorkerService.ask2ClientAboutGettingANewJob(request);
    }
}
