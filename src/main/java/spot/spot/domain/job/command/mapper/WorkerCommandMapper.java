package spot.spot.domain.job.command.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.command.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.query.util.DistanceCalculateUtil;
import spot.spot.domain.member.entity.Ability;
import spot.spot.domain.member.entity.AbilityType;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.entity.WorkerAbility;
import spot.spot.domain.member.repository.AbilityRepository;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AbilityRepository.class, DistanceCalculateUtil.class})
public interface WorkerCommandMapper {



    // 1) Registing request to Entity
    @Mapping(target = "member", source = "member")
    @Mapping(target = "introduction", source = "request.content")
    @Mapping(target = "workerAbilities", ignore = true) // WorkerAbility 매핑은 별도 처리
    Worker dtoToWorker(RegisterWorkerRequest request, Member member);


    // 2) 구직자와 강점의 교차테이블 생성
    default List<WorkerAbility> mapWorkerAbilities(List<AbilityType> strong, Worker worker, AbilityRepository abilityRepository) {
        if(strong == null || strong.isEmpty()) return new ArrayList<>();
        return strong.stream()
            .map(type -> {
                Ability ability = abilityRepository.findByType(type)
                    .orElseGet(() -> abilityRepository.save(Ability.builder().type(type).build()));

                return WorkerAbility.builder()
                    .worker(worker)
                    .ability(ability)
                    .build();
            })
            .collect(Collectors.toList());
    }

    // 3. 구직자 lat, lng -> POINT 객체
    default Point mapLatLngToPoint (double lat, double lng, GeometryFactory geometryFactory) {
        Point ans = geometryFactory.createPoint(new Coordinate(lng, lat));
        ans.setSRID(4326);
        return ans;
    }
}
