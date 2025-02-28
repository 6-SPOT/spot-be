package spot.spot.domain.pay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.service.Job4ClientService;
import spot.spot.domain.pay.entity.dto.request.PayApproveRequestDto;
import spot.spot.domain.pay.entity.dto.response.PayApproveResponse;
import spot.spot.domain.pay.service.PayService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay")
public class PayController {

    private final PayService payService;
    private final Job4ClientService job4ClientService;

    @PostMapping("/deposit")
    public ResponseEntity<PayApproveResponse> payApprove(@RequestBody @Valid PayApproveRequestDto request, Authentication auth) {
        Job job = job4ClientService.findByTid(request.tid());
        PayApproveResponse approve = payService.payApprove(auth.getName(),
                job,
                request.pgToken(),
                request.totalAmount());
        return ResponseEntity.ok().body(approve);
    }

}
