package spot.spot.domain.job.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job._docs.Job4WorkerDocs;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
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
}
