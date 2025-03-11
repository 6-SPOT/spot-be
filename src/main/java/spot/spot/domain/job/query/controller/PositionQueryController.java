package spot.spot.domain.job.query.controller;

import static spot.spot.global.util.ConstantUtil.AUTHORIZATION;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.spot.domain.job.query.controller._docs.PositionQueryDocs;
import spot.spot.domain.job.query.dto.request.PositionRequest;
import spot.spot.domain.job.query.service.PositionQueryService;

@RestController
@RequiredArgsConstructor
public class PositionQueryController implements PositionQueryDocs {

    private final PositionQueryService positionQueryService;

    @MessageMapping("/job/worker/real-time")
    public void pubMessage(PositionRequest request, @Header(AUTHORIZATION) final String atk) {
        positionQueryService.sendPosition(request, atk);
    }
}
