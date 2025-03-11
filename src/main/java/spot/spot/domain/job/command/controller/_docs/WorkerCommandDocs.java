package spot.spot.domain.job.command.controller._docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.request.ChangeStatusWorkerRequest;
import spot.spot.domain.job.command.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoClientsRequest;
import spot.spot.domain.job.command.dto.response.JobCertifiationResponse;

@Tag(
    name = "3. WORKER COMMAND API",
    description = "<br/> 구직자를 위한 CUD API"
)
public interface WorkerCommandDocs {

    @Operation(summary = "구직 등록하기",
        description = "Contnet-Type은 multipart/form-data로 해주세요. 강점 ENUM 값은 하단 AbilityType 참고 바랍니다.")
    @PutMapping
    public void registerWorker(RegisterWorkerRequest request);

    @Operation(summary = "일을 자신이 하겠다고 일 올린 사람한테 요청하기",
        description = "일 요청 성공 시, ATTENDER로 생성")
    @PostMapping
    public void askingJob2Client(@RequestBody ChangeStatusWorkerRequest request);

    @Operation(summary = "일을 시작하기",
        description = "일 상태: YES or NO -> START 변환")
    @PostMapping
    public void startJob(@RequestBody ChangeStatusWorkerRequest request);

    @Operation(summary = "의뢰인이 요청한 일 수락 혹은 거절",
        description = "일 상태: REQUEST -> YES or NO 변환")
    @PostMapping
    public void acceptJobRequestOfClient (@RequestBody YesOrNoClientsRequest request);

    @Operation(summary = "일 재개 응답",
        description = " 상태: SLEEP -> START 로 변환 + 취소 예약 철회")
    @PostMapping
    public void continueJob (ChangeStatusWorkerRequest request);

    @Operation(summary = "일 하는 것 증명 사진 제출")
    @PostMapping
    public JobCertifiationResponse certificateJob(ChangeStatusWorkerRequest request, MultipartFile file);

    @Operation(summary = "해결사가 주어진 의뢰를 다했을 때 누르는 버튼 용 API",
    description =  "START -> FINISH 로 상태 변화")
    @PatchMapping
    public void finishJob(@RequestBody ChangeStatusWorkerRequest request);

    @Operation(summary = "구직자 등록 삭제")
    @DeleteMapping
    public void deletingWorker();
}
