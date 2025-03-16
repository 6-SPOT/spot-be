package spot.spot.domain.job.v2.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.v2.query.util.SearchingJobQueryUtilV2;
import spot.spot.domain.job.v2.query.util.SearchingJobQueryUtilWithJpqlV2;
import spot.spot.domain.job.v2.query.util.SearchingJobQueryUtilWithNativeQueryV2;
import spot.spot.domain.job.v2.query.util.SearchingJobQueryUtilWithQuerydslV2;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.security.util.UserAccessUtil;

@Service
@Deprecated
@RequiredArgsConstructor
public class WorkerQueryServiceV2 {

    private final UserAccessUtil userAccessUtil;
    private final SearchingJobQueryUtilWithJpqlV2 searchingJobQueryUtilWithJpqlV2;
    private final SearchingJobQueryUtilWithNativeQueryV2 searchingJobQueryUtilWithNativeQueryV2;
    private final SearchingJobQueryUtilWithQuerydslV2 searchingJobQueryUtilWithQuerydslV2;

    public Slice<NearByJobResponse> getNearByJobListWithJPQL (Double lat, Double lng, int zoom, Pageable pageable){
        return getNearByJobList(searchingJobQueryUtilWithJpqlV2, lat, lng, zoom, pageable);
    }

    public Slice<NearByJobResponse> getNearByJobListWithNativeQuery (Double lat, Double lng, int zoom, Pageable pageable){
        return getNearByJobList(searchingJobQueryUtilWithNativeQueryV2, lat, lng, zoom, pageable);
    }

    public Slice<NearByJobResponse> getNearByJobListWithQueryDsl (Double lat, Double lng, int zoom, Pageable pageable){
        return getNearByJobList(searchingJobQueryUtilWithQuerydslV2, lat, lng, zoom, pageable);
    }


    private Slice<NearByJobResponse> getNearByJobList(SearchingJobQueryUtilV2 service, Double lat, Double lng, int zoom, Pageable pageable) {
        Member me = userAccessUtil.getMember();
        lat = (lat == null)? me.getLat() : lat;
        lng = (lng == null)? me.getLng() : lng;
        return service.findNearByJobs(lat, lng, zoom, pageable);
    }
}
