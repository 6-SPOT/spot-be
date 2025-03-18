package spot.spot.domain.job.v1.query.service._docs;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Service
@Deprecated
public interface SearchingJobQueryUtilV1 {
    // 1. 근처 사용자 찾아서 페이지 네이션 적용 -> JOB을 찾아서 Mapping 및 dist 계산 로직을 한 번 더 함.
    Slice<NearByJobResponse> findNearByJobs (double lat, double lng, int zoom, Pageable pageable);
}
