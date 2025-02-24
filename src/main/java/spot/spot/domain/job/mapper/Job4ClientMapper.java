package spot.spot.domain.job.mapper;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.AttenderResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.member.entity.Worker;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Job4ClientMapper {
    Job registerRequestToJob (String img, RegisterJobRequest request);

    @Mapping(source = "member.id", target = "id")
    @Mapping(source = "member.nickname", target = "name")
    @Mapping(source = "member.img", target = "profile_img")
    @Mapping(source = "member.lat", target = "lat")
    @Mapping(source = "member.lng", target = "lng")
    @Mapping(source = "workerAbilities", target = "workerAbilities")
    AttenderResponse toResponse(Worker worker);

    @IterableMapping(elementTargetType = AttenderResponse.class)
    List<AttenderResponse> toResponseList(List<Worker> workers);

}
