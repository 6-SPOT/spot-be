package spot.spot.domain.job.command.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.dto.request.ConfirmOrRejectRequest;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;

@Tag(
    name = "1. CLIENT COMMAND API",
    description = "<br/> 일 의뢰자를 위한 CUD API")
public interface ClientCommandDocs {
    // 1)
    @Operation(summary = "시킬 일 서버에 등록하기",
        description = "일의 메타데이터와 썸네일 사진을 올려주세요. Contnet-Type은 multipart/form-data,")
    @PutMapping
    public RegisterJobResponse registerJob(
        @Parameter(description = "업무 요청 정보 (밑에 RegisterJobRequest 참고 바람)", required = true)
        @RequestPart(value = "request")
        RegisterJobRequest request,
        @Parameter(description = "썸네일 이미지 파일")
        @RequestPart(value = "file", required = false )
        MultipartFile file
    );
    // 2)
    @Operation(summary = "해결사에게 일 의뢰하기",
        description = "요청한 job_id와 의뢰 맡길 해결사 id로 테이블 생성. 상태: REQUEST")
    @PostMapping
    public void askJob2Worker (@RequestBody ChangeStatusClientRequest request);
    // 3)
    @Operation(summary = "해결사의 신청 승낙",
        description = "요청한 job_id에 대한 해결사의 상태: ATTENDER -> YES 변환")
    @PostMapping
    public void acceptJobRequestOfWorker (@RequestBody YesOrNoWorkersRequest request);
    // 4)
    @Operation(summary = "일 철회 요청",
        description = "잠수탄 해결사에 대해 일 철회 요청 (상태: SLEEP 처리 -> 10분 후 CANCEL로 조절)")
    @PostMapping
    public void requestWithdrawal (@RequestBody ChangeStatusClientRequest request);
    // 5)
    @Operation(summary = "해결사의 일 완료 요청을 반려 시키거나 확정",
        description = "요청한 job_id에 대한 해결사의 상태: FINISH -> CONFIRM or REJECT")
    @PatchMapping
    public void confirmOrRejectJob(ConfirmOrRejectRequest request);
}
