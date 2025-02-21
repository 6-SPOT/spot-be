package spot.spot.domain.job.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.dto.request.RegisterWorkerRequest;
import spot.spot.domain.job.mapper.Job4WorkerMapper;
import spot.spot.domain.job.mapper.Job4WorkerMapperImpl;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.repository.AbilityRepository;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.member.repository.WorkerRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class Job4WorkerService {

    private final UserAccessUtil userAccessUtil;
    private final WorkerRepository workerRepository;
    private final Job4WorkerMapper job4WorkerMapper;
    private final AbilityRepository abilityRepository;

    public void registeringWorker(RegisterWorkerRequest request) {
        Member member = userAccessUtil.getMember().orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        Worker worker = job4WorkerMapper.dtoToWorker(request, member);
        workerRepository.save(worker);
        log.info(job4WorkerMapper.mapWorkerAbilities(request.strong(), worker, abilityRepository).toString());
    }

}
