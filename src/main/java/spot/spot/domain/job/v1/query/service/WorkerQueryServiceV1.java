package spot.spot.domain.job.v1.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.v1.query.service._docs.SearchingJobQueryUtilV1;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.security.util.UserAccessUtil;

@Slf4j
@Service
@Deprecated
@RequiredArgsConstructor
public class WorkerQueryServiceV1 {
    // Util
    private final UserAccessUtil userAccessUtil;
    // 거리 계산용 3가지
    private final SearchingJobQueryDslUtilV1 searchingJobQueryDslVersionUtilV1;
    private final SearchingJobJpqlQueryUtilV1 searchingJobJpqlQueryUtilV1;
    private final SearchingJobNativeQueryUtilV1 searchingJobNativeQueryUtilV1;

    public Slice<NearByJobResponse> getNearByJobListWithJPQL(Double lat, Double lng, int zoom, Pageable pageable) {
        return getNearByJobList(searchingJobJpqlQueryUtilV1, lat, lng, zoom, pageable);
    }

    public Slice<NearByJobResponse> getNearByJobListWithNativeQuery(Double lat, Double lng, int zoom, Pageable pageable) {
        return getNearByJobList(searchingJobNativeQueryUtilV1, lat, lng, zoom, pageable);
    }

    public Slice<NearByJobResponse> getNearByJobListWithQueryDsl(Double lat, Double lng, int zoom,Pageable pageable) {
        return getNearByJobList(searchingJobQueryDslVersionUtilV1, lat, lng, zoom, pageable);
    }

    private Slice<NearByJobResponse> getNearByJobList(SearchingJobQueryUtilV1 service, Double lat, Double lng, int zoom, Pageable pageable) {
        Member member = userAccessUtil.getMember();
        lat = (lat == null) ? member.getLat() : lat;
        lng = (lng == null) ? member.getLng() : lng;
        return service.findNearByJobs(lat, lng, zoom, pageable);
    }
}
