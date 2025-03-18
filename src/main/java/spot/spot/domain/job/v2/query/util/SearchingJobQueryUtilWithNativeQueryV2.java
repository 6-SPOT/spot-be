package spot.spot.domain.job.v2.query.util;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;
import spot.spot.domain.job.v2.query.repository.jpa.JobRepositoryV2;

@Service
@Deprecated
@RequiredArgsConstructor
public class SearchingJobQueryUtilWithNativeQueryV2 implements SearchingJobQueryUtilV2{

    private final DistanceCalculateUtil distanceCalculateUtil;
    private final JobRepositoryV2 jobRepositoryV2;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        List<NearByJobResponse> jobs = jobRepositoryV2.findNearByJobWithNativeQuery(lat, lng, dist,
            pageable.getPageSize() +1, offset);
        boolean hasNext = jobs.size() > pageable.getPageSize();
        if(hasNext) {
            jobs = jobs.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(jobs, pageable, hasNext);
    }
}
