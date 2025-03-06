package spot.spot.domain.job.mapper;

import com.querydsl.core.Tuple;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.*;
import spot.spot.domain.job.dto.request.RegisterJobRequest;
import spot.spot.domain.job.dto.response.AttenderResponse;
import spot.spot.domain.job.dto.response.JobSituationResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.entity.WorkerAbility;
import spot.spot.domain.pay.entity.PayHistory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Job4ClientMapper {
    /*
    * 1) 일 등록 (reqeust -> entity)
    * 2) 일 신청자 하나 변환 (entity -> response)
    * 3) 2번의 리스트 형태
    * 4) 3번 변환 시, 일에 필요한 능력을 교차테이블을 통해 String으로 변환하여 매핑하는 매소드
    * */

    // 1) 일 등록  (reqeust -> entity)
    @Mapping(source = "tid", target = "tid")
    Job registerRequestToJob (String img, RegisterJobRequest request, String tid);
    // 2) 일 신청자 하나 변환 (entity -> response)
    @Mapping(source = "member.id", target = "id")
    @Mapping(source = "member.nickname", target = "name")
    @Mapping(source = "member.img", target = "profile_img")
    @Mapping(source = "member.lat", target = "lat")
    @Mapping(source = "member.lng", target = "lng")
    @Mapping(source = "workerAbilities", target = "abilities", qualifiedByName = "mapAbilities")
    AttenderResponse toResponse(Worker worker);
    // 3) 2번의 List 형태
    @IterableMapping(elementTargetType = AttenderResponse.class)
    List<AttenderResponse> toResponseList(List<Worker> workers);
    // 4) 3번 변환 시, 일에 필요한 능력을 교차테이블을 통해 String으로 변환하여 매핑하는 매소드
    @Named("mapAbilities")
    default List<String> mapAbilities(List<WorkerAbility> workerAbilities) {
        return workerAbilities != null
            ? workerAbilities.stream()
            .map(workerAbility -> workerAbility.getAbility().getType().name())
            .distinct() // ✅ 중복 제거
            .collect(Collectors.toList())
            : Collections.emptyList();
    }
}
