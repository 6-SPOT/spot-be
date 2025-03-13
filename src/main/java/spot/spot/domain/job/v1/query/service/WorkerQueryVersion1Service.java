package spot.spot.domain.job.v1.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.util._docs.SearchingJobQueryUtil;
import spot.spot.domain.job.query.util.searching.SearchingJobJPQLQueryUtil;
import spot.spot.domain.job.query.util.searching.SearchingJobNativeQueryUtil;
import spot.spot.domain.job.query.util.searching.SearchingJobQueryDSLUtil;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerQueryVersion1Service {
    // Util
    private final UserAccessUtil userAccessUtil;
    // 거리 계산용 3가지
    private final SearchingJobJPQLQueryUtil searchingJobJPQLQueryUtil;
    private final SearchingJobNativeQueryUtil searchingJobNativeQueryUtil;
    private final SearchingJobQueryDSLUtil searchingJobQueryDSLUtil;

    public Slice<NearByJobResponse> getNearByJobListWithJPQL(Double lat, Double lng, int zoom, Pageable pageable) {
        return getNearByJobList(searchingJobJPQLQueryUtil, lat, lng, zoom, pageable);
    }

    public Slice<NearByJobResponse> getNearByJobListWithNativeQuery(Double lat, Double lng, int zoom, Pageable pageable) {
        return getNearByJobList(searchingJobNativeQueryUtil, lat, lng, zoom, pageable);
    }

    public Slice<NearByJobResponse> getNearByJobListWithQueryDsl(Double lat, Double lng, int zoom,Pageable pageable) {
        return getNearByJobList(searchingJobQueryDSLUtil, lat, lng, zoom, pageable);
    }

    private Slice<NearByJobResponse> getNearByJobList(SearchingJobQueryUtil service, Double lat, Double lng, int zoom, Pageable pageable) {
        Member member = userAccessUtil.getMember();
        lat = (lat == null) ? member.getLat() : lat;
        lng = (lng == null) ? member.getLng() : lng;
        return service.findNearByJobs(lat, lng, zoom, pageable);
    }
}
