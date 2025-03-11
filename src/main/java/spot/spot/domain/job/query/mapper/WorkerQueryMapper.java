package spot.spot.domain.job.query.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.command.dto.Location;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.util.JobUtil;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkerQueryMapper {

    @Mapping(target = "dist", ignore = true)
    NearByJobResponse toNearByJobResponse(Job job);

    default List<NearByJobResponse> toNearByJobResponseList(List<Job> jobs, Location location) {
        return jobs.stream()
            .map(job -> {
                NearByJobResponse response = toNearByJobResponse(job);
                double distance = JobUtil.calculateHaversineDistance(location.lat(), location.lng(), job.getLat(), job.getLng());
                return response.toBuilder().dist(distance).build(); //기존 객체를 복사 + dist만 변경
            })
            .toList();
    }
}
