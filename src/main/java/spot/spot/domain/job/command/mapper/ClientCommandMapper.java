package spot.spot.domain.job.command.mapper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.*;
import spot.spot.domain.job.command.dto.request.RegisterJobRequest;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.member.entity.Member;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientCommandMapper {
    // 1) 일 등록  (reqeust -> entity)
    @Mapping(target = "location", expression = "java(mapLatLngToPoint(request.lat(), request.lng(), geometryFactory))")
    Job registerRequestToJob (String img, RegisterJobRequest request, @Context GeometryFactory geometryFactory);

    // 2. from DTO to Entity
    @Mapping(target = "id", ignore = true)
    Matching toMatching (Member member, Job job, MatchingStatus status);

    default Point mapLatLngToPoint(double lat, double lng, GeometryFactory geometryFactory) {
        return geometryFactory.createPoint(new Coordinate(lat, lng));
    }
}
