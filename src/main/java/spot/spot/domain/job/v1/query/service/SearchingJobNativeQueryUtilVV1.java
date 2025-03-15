package spot.spot.domain.job.v1.query.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.command.dto.Location;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;
import spot.spot.domain.job.v1.query.mapper.WorkerQueryMapperV1;
import spot.spot.domain.job.v1.query.repository.jpa.JobRepositoryV1;
import spot.spot.domain.job.v1.query.service._docs.SearchingJobQueryVersionUtilV1;


@Service
@Deprecated
@RequiredArgsConstructor
public class SearchingJobNativeQueryUtilVV1 implements SearchingJobQueryVersionUtilV1 {
    private final JobRepositoryV1 jobRepositoryV1;
    private final DistanceCalculateUtil distanceCalculateUtil;
    private final WorkerQueryMapperV1 workerQueryMapperV1;

    @Override
    public Slice<NearByJobResponse> findNearByJobs(double lat, double lng, int zoom, Pageable pageable) {
        double dist = distanceCalculateUtil.convertZoomToRadius(zoom);
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        List<Job> jobs = jobRepositoryV1.findNearByJobWithNativeQuery(lat, lng, dist,
            pageable.getPageSize() + 1, offset);

        boolean hasNext = jobs.size() > pageable.getPageSize();
        if (hasNext) {
            jobs = jobs.subList(0, pageable.getPageSize());
        }

        List<NearByJobResponse> responseList = workerQueryMapperV1
            .toNearByJobResponseList(jobs, Location.builder().lat(lat).lng(lng).build());
        return new SliceImpl<>(responseList, pageable, hasNext);
    }
}
