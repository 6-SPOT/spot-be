package spot.spot.domain.job.query.util.searching;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.command.dto.Location;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.mapper.WorkerQueryMapper;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;

@Service
@RequiredArgsConstructor
public class SearchingJobJPQLQueryUtil implements SearchingJobQueryUtil {

    private final JobRepository jobRepository;
    private final DistanceCalculateUtil distanceCalculateUtil;
    private final WorkerQueryMapper workerQueryMapper;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        Slice<Job> jobs = jobRepository.findNearByJobWithJPQL(lat, lng,dist, pageable);
        List<NearByJobResponse> responseList = workerQueryMapper
            .toNearByJobResponseList(jobs.getContent(), Location.builder().lat(lat).lng(lng).build());
        return new SliceImpl<>(responseList, pageable, jobs.hasNext());
    }
}
