package spot.spot.domain.pay.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.service.Job4ClientService;
import spot.spot.domain.pay.entity.dto.PayReadyResponse;
import spot.spot.domain.pay.entity.dto.PayReadyResponseDto;

import java.util.List;


@SpringBootTest
@Transactional
@Slf4j
class PayServiceTest {

    @Autowired
    PayService payService;

    @Autowired
    Job4ClientService job4ClientService;

    @Test
    void payReadyElements() {
        PayReadyResponseDto payReadyResponseDto = payService.payReady("테스트유저", "전구 고쳐주세요", 10000, 0);
        log.info("redirect_pc_url = {}, redirect_mobile_url = {}", payReadyResponseDto.redirectPCUrl(), payReadyResponseDto.redirectMobileUrl());

    }

    @Test
    void payReadyByJobRegister() {
        RegisterJobRequest jobRequest = new RegisterJobRequest(
                "음쓰 버려주실 분~",
                "아파트 현관문 비번: 6541",
                10000,
                500,
                123.2324225,
                245.2242557
        );

        System.out.println(jobRequest);

        PayReadyResponseDto payReadyResponseDto = job4ClientService.registerJob(jobRequest);

    }
}