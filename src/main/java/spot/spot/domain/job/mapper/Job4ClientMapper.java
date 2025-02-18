package spot.spot.domain.job.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.entity.Job;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Job4ClientMapper {
    Job registerRequestToJob (String img, RegisterJobRequest request);


}
