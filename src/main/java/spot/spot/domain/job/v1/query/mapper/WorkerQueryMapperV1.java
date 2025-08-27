package spot.spot.domain.job.v1.query.mapper;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.command.dto.Location;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.util._docs.DistanceCalculateUtilDocs;

@Deprecated
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkerQueryMapperV1 {

    @Mapping(target = "dist", ignore = true)
    @Mapping(target = "picture", source = "img")
    NearByJobResponse toNearByJobResponse(Job job);

    default List<NearByJobResponse> toNearByJobResponseList(List<Job> jobs, Location location) {
        return jobs.parallelStream()
            .map(job -> {
                NearByJobResponse response = toNearByJobResponse(job);
                double distance = DistanceCalculateUtilDocs.calculateHaversineDistance(location.lat(), location.lng(), job.getLat(), job.getLng());
                return response.toBuilder().dist(distance).build(); //기존 객체를 복사 + dist만 변경
            })
            .toList();
    }
}
