package spot.spot.domain.job.query.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.CertificationImgResponse;
import spot.spot.domain.job.query.dto.response.JobDetailResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.dsl.SearchingListQueryDsl;
import spot.spot.domain.job.query.repository.dsl.SearchingOneQueryDsl;
import spot.spot.domain.job.query.service._docs.WorkerQueryServiceDocs;
import spot.spot.domain.job.query.util.searching.SearchingJobQueryDSLUtil;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerQueryService implements WorkerQueryServiceDocs {
    // Util
    private final UserAccessUtil userAccessUtil;
    private final SearchingJobQueryDSLUtil jobSearchQueryDSLService;
    // queryDSL
    private final SearchingOneQueryDsl searchingOneQueryDsl;
    private final SearchingListQueryDsl searchingListQueryDsl;

    public Slice<NearByJobResponse> getNearByJobList(Double lat, Double lng, int zoom, Pageable pageable) {
        Member member = userAccessUtil.getMember();
        lat = lat == null? member.getLat() : lat;
        lng = lng == null? member.getLng(): lng;
        return jobSearchQueryDSLService.findNearByJobs(lat, lng, zoom, pageable);
    }

    public JobDetailResponse getOneJob (long jobId) {
        Member me = userAccessUtil.getMember();
        return searchingOneQueryDsl.findOneJobDetail(jobId, me.getId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public List<JobSituationResponse> getMyJobSituations() {
        Member me = userAccessUtil.getMember();
        return searchingListQueryDsl.findJobSituationsByWorker(me.getId());
    }


    public List<CertificationImgResponse> getWorkersCertificationImgList(long jobId, long workerId) {
        return List.of();
    }
}
