package spot.spot.domain.job.query.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.query.dto.response.JobDetailResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.dsl.SearchingListQueryDsl;
import spot.spot.domain.job.query.repository.dsl.SearchingOneQueryDsl;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.query.util.searching.SearchingJobJPQLQueryUtil;
import spot.spot.domain.job.query.util.searching.SearchingJobNativeQueryUtil;
import spot.spot.domain.job.query.util.searching.SearchingJobQueryDSLUtil;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerQueryService {
    // Util
    private final UserAccessUtil userAccessUtil;
    // 거리 계산용 3가지
    private final SearchingJobJPQLQueryUtil searchingJobJPQLService;
    private final SearchingJobNativeQueryUtil jobSearchNativeService;
    private final SearchingJobQueryDSLUtil jobSearchQueryDSLService;
    // queryDSL
    private final SearchingOneQueryDsl searchingOneQueryDsl;
    private final SearchingListQueryDsl searchingListQueryDsl;

    public Slice<NearByJobResponse> getNearByJobList(String impl, Double lat, Double lng, int zoom, Pageable pageable) {
        Member member = userAccessUtil.getMember();
        lat = lat == null? member.getLat() : lat;
        lng = lng == null? member.getLng() : lng;

        SearchingJobQueryUtil service = switch (impl.toLowerCase()) {
            case "jpql" -> searchingJobJPQLService;
            case "native" -> jobSearchNativeService;
            case "dsl" -> jobSearchQueryDSLService;
            default -> throw new GlobalException(ErrorCode.INVALID_SEARCH_METHOD);
        };
        return service.findNearByJobs(lat, lng, zoom, pageable);
    }
    // 일 하나 상세 확인
    public JobDetailResponse getOneJob (long jobId) {
        Member me = userAccessUtil.getMember();
        return searchingOneQueryDsl.findOneJobDetail(jobId, me.getId()).orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public List<JobSituationResponse> getMyJobSituations() {
        Member me = userAccessUtil.getMember();
        return searchingListQueryDsl.findJobSituationsByWorker(me.getId());
    }
}
