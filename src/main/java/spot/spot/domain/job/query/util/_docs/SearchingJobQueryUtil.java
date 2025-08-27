package spot.spot.domain.job.query.util._docs;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Service
public interface SearchingJobQueryUtil {
    // 1. 근처 사용자 찾아서 페이지 네이션 적용
    Slice<NearByJobResponse> findNearByJobs (double lat, double lng, int zoom, Pageable pageable);
}
