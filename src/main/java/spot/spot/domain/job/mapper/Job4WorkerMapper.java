package spot.spot.domain.job.mapper;

import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.job.dto.Location;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.dto.response.JobDetailResponse;
import spot.spot.domain.job.dto.response.NearByJobResponse;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.service.JobUtil;
import spot.spot.domain.member.entity.Ability;
import spot.spot.domain.member.entity.AbilityType;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.entity.WorkerAbility;
import spot.spot.domain.member.repository.AbilityRepository;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AbilityRepository.class, JobUtil.class})
public interface Job4WorkerMapper {

    /*
    *   1) 구직자 등록 (request -> entity)
    *   2) 근처 일 하나 변환 (entity -> response)
    *   3) Worker가 입력한 자신의 능력들과 Worker 본인을 교차테이블로 연관관계 짓기 (default)
    *   4) 2번을 List로 변환
    * */

    // 1) Registing request to Entity
    @Mapping(target = "member", source = "member")
    @Mapping(target = "introduction", source = "request.content")
    @Mapping(target = "workerAbilities", ignore = true) // WorkerAbility 매핑은 별도 처리
    Worker dtoToWorker(RegisterWorkerRequest request, Member member);
    // 2) 근처 일 하나 변환 (entity to response)
    @Mapping(target = "dist", ignore = true)
    NearByJobResponse toNearByJobResponse(Job job);



    // 3) Worker가 입력한 자신의 능력들과 Worker 본인을 교차테이블로 연관관계 짓기 (default)
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
    // 4) 2번을 리스트로 변환
    default List<NearByJobResponse> toNearByJobResponseList(List<Job> jobs, Location location) {
        return jobs.stream()
            .map(job -> {
                NearByJobResponse response = toNearByJobResponse(job);
                double distance = JobUtil.calculateHaversineDistance(location.lat(), location.lng(), job.getLat(), job.getLng());
                return response.toBuilder().dist(distance).build(); //기존 객체를 복사 + dist만 변경
            })
            .toList();
    }
}
