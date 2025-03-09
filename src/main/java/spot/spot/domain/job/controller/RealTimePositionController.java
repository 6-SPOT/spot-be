package spot.spot.domain.job.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import spot.spot.domain.job.dto.request.PositionRequest;
import spot.spot.domain.job.service.RealTimePositionService;
import static spot.spot.global.util.ConstantUtil.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class RealTimePositionController {
    private final RealTimePositionService realTimePositionService;

    @MessageMapping("/job")
    public void pubMessage(PositionRequest request, @Header(AUTHORIZATION) final String atk) {
        realTimePositionService.sendPosition(request, atk);
    }

}
