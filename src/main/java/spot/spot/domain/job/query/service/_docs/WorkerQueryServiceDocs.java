package spot.spot.domain.job.query.service._docs;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.CertificationImgResponse;
import spot.spot.domain.job.query.dto.response.JobDetailResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

public interface WorkerQueryServiceDocs {
    // 해결사 근처의 일 리스트 반환 -> 지도 위에 좌표로 띄우는 것 가정
    public Slice<NearByJobResponse> getNearByJobList(Double lat, Double lng, int zoom, Pageable pageable);
    // 일 하나 상세 확인
    public JobDetailResponse getOneJob (long jobId);
    // 해결사의 일 현황표
    public List<JobSituationResponse> getMyJobSituations();
    // 해결사의 인증 사진 리스트 얻기
    public List<CertificationImgResponse> getWorkersCertificationImgList(long jobId, long workerId);
}
