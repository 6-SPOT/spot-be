package spot.spot.domain.job.command.mapper;

import org.mapstruct.*;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.entity.Job;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientCommandMapper {
    // 1) 일 등록  (reqeust -> entity)
    @Mapping(source = "tid", target = "tid")
    Job registerRequestToJob (String img, RegisterJobRequest request, String tid);
}
