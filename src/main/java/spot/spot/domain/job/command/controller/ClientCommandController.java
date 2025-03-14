package spot.spot.domain.job.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.controller._docs.ClientCommandDocs;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.dto.request.ConfirmOrRejectRequest;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.service.ClientCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/job")
public class ClientCommandController implements ClientCommandDocs {

    private final ClientCommandService clientCommandService;

    @PutMapping(value = "/register",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegisterJobResponse registerJob(
        @RequestPart(value = "request") RegisterJobRequest request,
        @RequestPart(value = "file", required = false ) MultipartFile file
    ) {
        return clientCommandService.registerJob(request, file);
    }

    @PostMapping("/choice")
    public void askJob2Worker(@RequestBody ChangeStatusClientRequest request) {
        clientCommandService.askingJob2Worker(request);
    }

    @PostMapping("/yes-or-no")
    public void acceptJobRequestOfWorker(@RequestBody YesOrNoWorkersRequest request) {
        clientCommandService.yesOrNo2RequestOfWorker(request);
    }

    @PostMapping("/withdrawal")
    public void requestWithdrawal(@RequestBody ChangeStatusClientRequest request) {
        clientCommandService.requestWithdrawal(request);
    }

    @PostMapping("/withdrawal/test")
    public void requestWithdrawalTest(@RequestBody ChangeStatusClientRequest request) {
        clientCommandService.requestWithdrawalTest(request);
    }

    @PatchMapping("/confirm-or-reject")
    public void confirmOrRejectJob(@RequestBody ConfirmOrRejectRequest request) {
        clientCommandService.confirmOrRejectJob(request);
    }
}
