package spot.spot.domain.job.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job._docs.WorkerDocs;
import spot.spot.domain.job.dto.request.ChangeStatusWorkerRequest;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.request.YesOrNoClientsRequest;
import spot.spot.domain.job.dto.response.JobCertifiationResponse;
import spot.spot.domain.job.dto.response.JobDetailResponse;
import spot.spot.domain.job.dto.response.JobSituationResponse;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.service.WorkerService;

@RestController
@RequestMapping("/api/job/worker")
@RequiredArgsConstructor
public class WorkerController implements WorkerDocs {

    private final WorkerService workerService;

    @PutMapping("/register")
    public void registerWorker(@RequestBody RegisterWorkerRequest request) {
        workerService.registeringWorker(request);
    }

    @GetMapping(value = "/search")
    public Slice<NearByJobResponse> nearByJobs(
        @RequestParam(required = false) Double lat,
        @RequestParam(required = false) Double lng,
        @RequestParam(required = false, defaultValue = "21") Integer zoom,
        Pageable pageable) {
        return workerService.getNearByJobList("dsl", lat, lng, zoom, pageable);
    }

    @GetMapping(value = "/get")
    public JobDetailResponse getOneJob(@RequestParam  long id) {
        return workerService.getOneJob(id);
    }

    @PostMapping("/request")
    public void askingJob2Client(@RequestBody ChangeStatusWorkerRequest request) {
        workerService.askingJob2Client(request);
    }

    @PostMapping("/start")
    public void startJob(@RequestBody ChangeStatusWorkerRequest request) {
        workerService.startJob(request);
    }

    @PostMapping("/yes-or-no")
    public void acceptJobRequestOfClient(YesOrNoClientsRequest request) {
        workerService.yesOrNo2RequestOfClient(request);
    }

    @PostMapping("/continue")
    public void continueJob(@RequestBody ChangeStatusWorkerRequest request) {
        workerService.contiuneJob(request);
    }

    @PostMapping(value = "/certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JobCertifiationResponse certificateJob(
        @RequestPart(value = "request") ChangeStatusWorkerRequest request,
        @RequestPart(value = "file") MultipartFile file) {
        return workerService.certificateJob(request, file);
    }

    @PatchMapping("/finish")
    public void finishJob(@RequestBody ChangeStatusWorkerRequest request) {
        workerService.finishingJob(request);
    }

    @GetMapping("/dash-board")
    public List<JobSituationResponse> getMyJobSituations() {
        return workerService.getMyJobSituations();
    }

    @DeleteMapping("/delete")
    public void deletingWorker() {
        workerService.deleteWorker();
    }
}
