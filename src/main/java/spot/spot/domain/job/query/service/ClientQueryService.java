package spot.spot.domain.job.query.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.job.query.service._docs.ClientQueryServiceDocs;
import spot.spot.domain.job.query.util.calculate.DistanceCalculateUtil;
import spot.spot.domain.job.query.dto.response.AttenderResponse;
import spot.spot.domain.job.query.dto.response.NearByWorkersResponse;
import spot.spot.domain.job.query.mapper.ClientQueryMapper;
import spot.spot.domain.job.query.repository.dsl.SearchingListQueryDsl;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.Worker;
import spot.spot.domain.member.mapper.MemberMapper;
import spot.spot.domain.member.repository.MemberQueryRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientQueryService implements ClientQueryServiceDocs {
    // Util
    private final DistanceCalculateUtil distanceCalculateUtil;
    private final UserAccessUtil userAccessUtil;
    // Mapper
    private final ClientQueryMapper clientQueryMapper;
    private final MemberMapper memberMapper;
    private final JobRepository jobRepository;
    // JPA
    private final MemberQueryRepository memberQueryRepository;
    // Query dsl
    private final SearchingListQueryDsl searchingListQueryDsl;


    public List<NearByWorkersResponse> findNearByWorkers(double lat, double lng, int zoomLevel) {
        return memberMapper.toDtoList(memberQueryRepository.findWorkerNearByMember(lat, lng, distanceCalculateUtil.convertZoomToRadius(zoomLevel)));
    }

    @Transactional(readOnly = true)
    public Slice<AttenderResponse> findJobAttenderList(long jobId, Pageable pageable) {
        Slice<Worker> workers = searchingListQueryDsl.findWorkersByJobId(jobId, pageable);
        List<AttenderResponse> responseList = clientQueryMapper.toResponseList(workers.getContent());
        return new SliceImpl<>(responseList, pageable, workers.hasNext());
    }

    public List<JobSituationResponse> getSituationsByOwner() {
        Member owner = userAccessUtil.getMember();
        return searchingListQueryDsl.findJobSituationsByOwner(owner.getId());
    }

    public Job findByTid(String tid) {
        return jobRepository.findByTid(tid).orElseThrow(() -> new GlobalException(ErrorCode.INVALID_TITLE));
    }

    public Job findById(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> new GlobalException(ErrorCode.JOB_NOT_FOUND));
    }
}
