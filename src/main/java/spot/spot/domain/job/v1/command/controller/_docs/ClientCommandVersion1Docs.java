package spot.spot.domain.job.v1.command.controller._docs;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;

@Tag(name= "나. (V1) CLIENT COMMAND API ", description = "<br/> 의뢰인 CUD API(첫 구현)")
public interface ClientCommandVersion1Docs {
    public void requestWithdrawalTest(@RequestBody ChangeStatusClientRequest request);
}
