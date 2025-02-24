package spot.spot.domain.job.service.searching;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.dto.response.NearByJobResponse;

@Service
public interface JobSearchService {
    Slice<NearByJobResponse> findNearByJobs (double lat, double lng, int zoom, Pageable pageable);
}
