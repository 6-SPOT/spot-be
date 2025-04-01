package spot.spot.domain.job.query.util.searching;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.query.util.calculate.DistanceCalculateUtil;

@Service
@RequiredArgsConstructor
public class SearchingJobJPQLQueryUtil implements SearchingJobQueryUtil {

    private final JobRepository jobRepository;
    private final DistanceCalculateUtil distanceCalculateUtil;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        return jobRepository.findNearByJobWithJPQL(lat,lng,zoom,pageable);
    }
}
