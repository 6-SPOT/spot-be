package spot.spot.domain.member.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.dto.response.NearByWorkersResponse;
import spot.spot.domain.member.entity.Member;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface MemberMapper {

    @Mapping(source = "nickname", target = "name")
    @Mapping(source = "img", target = "profile_img")
    NearByWorkersResponse toDto(Member member);

    List<NearByWorkersResponse> toDtoList(List<Member> members);
}
