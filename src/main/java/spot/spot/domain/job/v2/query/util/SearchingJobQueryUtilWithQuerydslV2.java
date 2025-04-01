package spot.spot.domain.job.v2.query.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.util.calculate.DistanceCalculateUtil;
import spot.spot.domain.job.v2.query.repository.dsl.SearchingListQueryDslv2;

@Service
@Deprecated
@RequiredArgsConstructor
public class SearchingJobQueryUtilWithQuerydslV2 implements SearchingJobQueryUtilV2{

    private final SearchingListQueryDslv2 searchingListQueryDslv2;
    private final DistanceCalculateUtil distanceCalculateUtil;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom,
        Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        return searchingListQueryDslv2.findNearByJobsWithQueryDSL(lat, lng, dist, pageable);
    }
}
