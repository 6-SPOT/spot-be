package spot.spot.domain.job.mapper;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.member.entity.Ability;
import spot.spot.domain.member.entity.AbilityType;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.entity.WorkerAbility;
import spot.spot.domain.member.repository.AbilityRepository;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
    AbilityRepository.class})
public interface Job4WorkerMapper {

    @Mapping(target = "member", source = "member")
    @Mapping(target = "introduction", source = "request.content")
    @Mapping(target = "workerAbilities", ignore = true) // WorkerAbility 매핑은 별도 처리
    Worker dtoToWorker(RegisterWorkerRequest request, Member member);

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
}
