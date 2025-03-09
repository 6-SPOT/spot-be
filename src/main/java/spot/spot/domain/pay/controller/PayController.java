package spot.spot.domain.pay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.service.ClientService;
import spot.spot.domain.pay.entity.dto.request.PayApproveRequestDto;
import spot.spot.domain.pay.entity.dto.request.PayReadyRequestDto;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponseDto;
import spot.spot.domain.pay.entity.dto.response.PayReadyResponseDto;
import spot.spot.domain.pay.service.PayService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay")
@Slf4j
public class PayController {

    private final PayService payService;
    private final ClientService clientService;

    @PostMapping("/deposit")
    public PayApproveResponseDto payApprove(@Valid @RequestBody PayApproveRequestDto request, Authentication auth) {
        Job job = clientService.findByTid(request.tid());
        return payService.payApprove(
                auth.getName(),
                job,
                request.pgToken(),
                request.totalAmount());
    }

    @PostMapping("/ready")
    public PayReadyResponseDto payReady(@Valid @RequestBody PayReadyRequestDto request, Authentication auth) {
        Job findJob = clientService.findById(request.jobId());
        PayReadyResponseDto payReadyResponseDto = payService.payReady(auth.getName(), request.content(), request.amount(), request.point(), findJob);
        String tid = payReadyResponseDto.tid();
        clientService.updateTidToJob(findJob, tid);
        return payReadyResponseDto;
    }
}
