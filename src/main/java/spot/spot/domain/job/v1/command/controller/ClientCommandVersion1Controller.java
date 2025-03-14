package spot.spot.domain.job.v1.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.service.ClientCommandService;
import spot.spot.domain.job.v1.command.controller._docs.ClientCommandVersion1Docs;
import spot.spot.domain.job.v1.command.service.ClientCommandVersion1Service;

@RestController
@RequestMapping("/api/job/v1")
@RequiredArgsConstructor
public class ClientCommandVersion1Controller implements ClientCommandVersion1Docs {

    private final ClientCommandVersion1Service clientCommandVersion1Service;

    @PostMapping("/withdrawal/test")
    public void requestWithdrawalTest(@RequestBody ChangeStatusClientRequest request) {
        clientCommandVersion1Service.requestWithdrawalTest(request);
    }
}
