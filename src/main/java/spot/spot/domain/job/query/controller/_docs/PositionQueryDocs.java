package spot.spot.domain.job.query.controller._docs;


import static spot.spot.global.util.ConstantUtil.AUTHORIZATION;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import spot.spot.domain.job.query.dto.request.PositionRequest;

@Tag(
    name = "5. POSITION QUERY API",
    description = "<br/> 해결사의 실시간 위치 확인 용 API")
public interface PositionQueryDocs {
    @Operation(summary = "해결사의 실시간 위치 보내는 API SOCKET")
    @MessageMapping
    public void pubMessage(PositionRequest request, @Header(AUTHORIZATION) final String atk);
}
