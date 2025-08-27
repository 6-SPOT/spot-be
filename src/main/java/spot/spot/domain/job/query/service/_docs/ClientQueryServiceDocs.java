package spot.spot.domain.job.query.service._docs;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.dto.response.AttenderResponse;
import spot.spot.domain.job.query.dto.response.NearByWorkersResponse;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

public interface ClientQueryServiceDocs {
    // 1. 의뢰인이 일을 올린 근처의 해결사 찾기
    public List<NearByWorkersResponse> findNearByWorkers(double lat, double lng, int zoomLevel);
    // 2. 일에 대한 신청자 리스트 얻기
    public Slice<AttenderResponse> findJobAttenderList(long jobId, Pageable pageable);
    // 3. 일 현황 표 얻기
    public List<JobSituationResponse> getSituationsByOwner();
    // 4. tid (결제 번호)로 일 찾기
    public Job findByTid(String tid);
    // 5. 아이디로 일 찾기
    public Job findById(Long jobId);

}
