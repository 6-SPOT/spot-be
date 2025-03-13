package spot.spot.domain.job.command.service._docs;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spot.spot.domain.job.command.dto.request.ChangeStatusClientRequest;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.dto.request.YesOrNoWorkersRequest;
import spot.spot.domain.job.command.dto.response.RegisterJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.dto.response.FcmDTO;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

public interface ClientCommandServiceDocs {
    // 1) 일 등록
    public RegisterJobResponse registerJob(RegisterJobRequest request, MultipartFile file);
    // 2) 해결사에게 일 의뢰
    public void askingJob2Worker (ChangeStatusClientRequest request);
    // 3) 일 수락 혹은 거절
    public void yesOrNo2RequestOfWorker(YesOrNoWorkersRequest request);
    // 4) NO Show 사용자를 위해 일 철회 요청
    public void requestWithdrawal(ChangeStatusClientRequest request);
    // 5) 결제 준비가 되면 일에 일치하는 tid값 넣어주기
    public Job updateTidToJob(Job findJob, String tid);
    // 6) 일 완료 확정 or 거절
    public void confirmOrRejectJob(YesOrNoWorkersRequest request);
}
