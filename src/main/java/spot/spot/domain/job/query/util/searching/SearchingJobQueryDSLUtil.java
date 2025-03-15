package spot.spot.domain.job.query.util.searching;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.dsl.SearchingListQueryDsl;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;

@Service
@RequiredArgsConstructor
public class SearchingJobQueryDSLUtil implements SearchingJobQueryUtil {

    private final SearchingListQueryDsl jobQueryDsl;
    private final DistanceCalculateUtil distanceCalculateUtil;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        return jobQueryDsl.findNearByJobsWithQueryDSL(lat,lng, dist, pageable);
    }
}
