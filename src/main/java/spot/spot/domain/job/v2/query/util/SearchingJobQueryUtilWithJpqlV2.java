package spot.spot.domain.job.v2.query.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.util.calculate.DistanceCalculateUtil;
import spot.spot.domain.job.v2.query.repository.jpa.JobRepositoryV2;

@Service
@Deprecated
@RequiredArgsConstructor
public class SearchingJobQueryUtilWithJpqlV2 implements SearchingJobQueryUtilV2{

    private final DistanceCalculateUtil distanceCalculateUtil;
    private final JobRepositoryV2 jobRepositoryV2;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom,
        Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        return jobRepositoryV2.findNearByJobWithJPQL(lat, lng, zoom, pageable);
    }
}
