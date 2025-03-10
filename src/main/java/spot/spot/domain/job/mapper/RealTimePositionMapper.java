package spot.spot.domain.job.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.dto.request.PositionRequest;
import spot.spot.domain.job.dto.response.PositionResponse;
import spot.spot.domain.member.entity.Member;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RealTimePositionMapper {

    @Mapping(source = "positionRequest.jobId", target = "jobId")
    @Mapping(source = "positionRequest.lat", target = "lat")
    @Mapping(source = "positionRequest.lng", target = "lng")
    @Mapping(source = "member.id", target = "senderId")
    @Mapping(source = "member.nickname", target = "senderName")
    @Mapping(source = "member.img", target = "senderImg")
    @Mapping(target = "sendDateTime", expression = "java(System.currentTimeMillis())")
    PositionResponse toPositionResponse(Member member, PositionRequest positionRequest);
}
