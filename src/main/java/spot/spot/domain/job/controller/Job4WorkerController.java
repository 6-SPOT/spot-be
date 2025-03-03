package spot.spot.domain.job.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job._docs.Job4WorkerDocs;
import spot.spot.domain.job.dto.request.Job2WorkerRequest;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.request.YesOrNo2ClientsRequest;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.service.Job4WorkerService;
import spot.spot.global.logging.Logging;

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
    public void askingJob2Client(@RequestBody Job2WorkerRequest request) {
        job4WorkerService.askingJob2Client(request);
    }

    @PostMapping("/start")
    public void startJob(@RequestBody Job2WorkerRequest request) {
        job4WorkerService.startJob(request);
    }

    @PostMapping("/yes-or-no")
    public void acceptJobRequestOfClient(YesOrNo2ClientsRequest request) {
        job4WorkerService.yesOrNo2RequestOfClient(request);
    }

    @PostMapping("/continue")
    public void continueJob(@RequestBody Job2WorkerRequest request) {
        job4WorkerService.contiuneJob(request);
    }

    @PutMapping(value = "/certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void certificateJob(
        @RequestPart(value = "request") Job2WorkerRequest request,
        @RequestPart(value = "file") MultipartFile file) {
        job4WorkerService.certificateJob(request, file);
    }


}
