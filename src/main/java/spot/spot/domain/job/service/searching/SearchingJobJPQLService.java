package spot.spot.domain.job.service.searching;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.dto.Location;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.mapper.WorkerMapper;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.job.service.SearchingJobService;
import spot.spot.domain.job.util.JobUtil;

@Service
@RequiredArgsConstructor
public class SearchingJobJPQLService implements SearchingJobService {

    private final JobRepository jobRepository;
    private final JobUtil jobUtil;
    private final WorkerMapper workerMapper;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = jobUtil.convertZoomToRadius(zoom);
        Slice<Job> jobs = jobRepository.findNearByJobWithJPQL(lat, lng,dist, pageable);
        List<NearByJobResponse> responseList = workerMapper
            .toNearByJobResponseList(jobs.getContent(), Location.builder().lat(lat).lng(lng).build());
        return new SliceImpl<>(responseList, pageable, jobs.hasNext());
    }
}
