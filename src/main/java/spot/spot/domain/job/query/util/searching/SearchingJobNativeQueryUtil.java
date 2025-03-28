package spot.spot.domain.job.query.util.searching;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;

@Service
@RequiredArgsConstructor
public class SearchingJobNativeQueryUtil implements SearchingJobQueryUtil {

    private final JobRepository jobRepository;
    private final DistanceCalculateUtil distanceCalculateUtil;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        List<NearByJobResponse> jobs = jobRepository.findNearByJobWithNativeQuery(lat, lng, dist,
            pageable.getPageSize() +1, offset);
        boolean hasNext = jobs.size() > pageable.getPageSize();
        if(hasNext) {
            jobs = jobs.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(jobs, pageable, hasNext);

    }
}
