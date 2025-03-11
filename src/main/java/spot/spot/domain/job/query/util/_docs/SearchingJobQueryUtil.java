package spot.spot.domain.job.query.util._docs;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Service
public interface SearchingJobQueryUtil {
    Slice<NearByJobResponse> findNearByJobs (double lat, double lng, int zoom, Pageable pageable);
}
