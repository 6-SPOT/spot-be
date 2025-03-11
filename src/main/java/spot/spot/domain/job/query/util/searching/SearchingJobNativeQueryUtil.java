package spot.spot.domain.job.query.util.searching;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.command.mapper.WorkerCommandMapper;
import spot.spot.domain.job.command.dto.Location;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.mapper.WorkerQueryMapper;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.command.util.JobUtil;

@Service
@RequiredArgsConstructor
public class SearchingJobNativeQueryUtil implements SearchingJobQueryUtil {

    private final JobRepository jobRepository;
    private final JobUtil jobUtil;
    private final WorkerQueryMapper workerQueryMapper;

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

        List<NearByJobResponse> responseList = workerQueryMapper
            .toNearByJobResponseList(jobs, Location.builder().lat(lat).lng(lng).build());
        return new SliceImpl<>(responseList, pageable, hasNext);

    }
}
