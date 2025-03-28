package spot.spot.domain.job.command.service._docs;

import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.request.ChangeStatusWorkerRequest;
import spot.spot.domain.job.command.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoClientsRequest;
import spot.spot.domain.job.command.dto.response.JobCertifiationResponse;

public interface WorkerCommandServiceDocs {
    // 일 등록하기
    public void registeringWorker(RegisterWorkerRequest request);
    // 일 신청하기
    public void askingJob2Client(ChangeStatusWorkerRequest request);
    // 일 시작하기
    public void startJob (ChangeStatusWorkerRequest request);
    // 의뢰인이 보낸 요청 승낙하기 혹은 거절하기
    public void yesOrNo2RequestOfClient(YesOrNoClientsRequest request);
    // 일 재개 하기
    public void continueJob(ChangeStatusWorkerRequest request);
    // 일 인증 사진 올리기
    public JobCertifiationResponse certificateJob(ChangeStatusWorkerRequest request, MultipartFile file);
    // 일 끝내기
    public void finishingJob(ChangeStatusWorkerRequest request);
    // 구직 등록 삭제
    public void deleteWorker();
}
