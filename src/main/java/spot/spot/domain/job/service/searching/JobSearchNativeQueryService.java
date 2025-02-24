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
import spot.spot.domain.job.mapper.Job4WorkerMapper;
import spot.spot.domain.job.repository.jpa.JobRepository;
import spot.spot.domain.job.service.JobUtil;

@Service
@RequiredArgsConstructor
public class JobSearchNativeQueryService implements JobSearchService{

    private final JobRepository jobRepository;
    private final JobUtil jobUtil;
    private final Job4WorkerMapper job4WorkerMapper;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = jobUtil.convertZoomToRadius(zoom);
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        List<Job> jobs = jobRepository.findNearByJobWithNativeQuery(lat, lng, dist,
            pageable.getPageSize() +1, offset);

        boolean hasNext = jobs.size() > pageable.getPageSize();
        if(hasNext) {
            jobs = jobs.subList(0, pageable.getPageSize());
        }

        List<NearByJobResponse> responseList = job4WorkerMapper
            .toNearByJobResponseList(jobs, Location.builder().lat(lat).lng(lng).build());
        return new SliceImpl<>(responseList, pageable, hasNext);

    }
}
