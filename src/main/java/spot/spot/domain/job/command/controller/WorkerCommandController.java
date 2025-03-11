package spot.spot.domain.job.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.controller._docs.WorkerCommandDocs;
import spot.spot.domain.job.command.dto.request.ChangeStatusWorkerRequest;
import spot.spot.domain.job.command.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoClientsRequest;
import spot.spot.domain.job.command.dto.response.JobCertifiationResponse;
import spot.spot.domain.job.command.service.WorkerCommandService;

@RestController
@RequestMapping("/api/job/worker")
@RequiredArgsConstructor
public class WorkerCommandController implements WorkerCommandDocs {

    private final WorkerCommandService workerCommandService;

    @PutMapping("/register")
    public void registerWorker(@RequestBody RegisterWorkerRequest request) {
        workerCommandService.registeringWorker(request);
    }
    @PostMapping("/request")
    public void askingJob2Client(@RequestBody ChangeStatusWorkerRequest request) {
        workerCommandService.askingJob2Client(request);
    }

    @PostMapping("/start")
    public void startJob(@RequestBody ChangeStatusWorkerRequest request) {
        workerCommandService.startJob(request);
    }

    @PostMapping("/yes-or-no")
    public void acceptJobRequestOfClient(YesOrNoClientsRequest request) {
        workerCommandService.yesOrNo2RequestOfClient(request);
    }

    @PostMapping("/continue")
    public void continueJob(@RequestBody ChangeStatusWorkerRequest request) {
        workerCommandService.contiuneJob(request);
    }

    @PostMapping(value = "/certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JobCertifiationResponse certificateJob(
        @RequestPart(value = "request") ChangeStatusWorkerRequest request,
        @RequestPart(value = "file") MultipartFile file) {
        return workerCommandService.certificateJob(request, file);
    }

    @PatchMapping("/finish")
    public void finishJob(@RequestBody ChangeStatusWorkerRequest request) {
        workerCommandService.finishingJob(request);
    }

    @DeleteMapping("/delete")
    public void deletingWorker() {
        workerCommandService.deleteWorker();
    }

}
