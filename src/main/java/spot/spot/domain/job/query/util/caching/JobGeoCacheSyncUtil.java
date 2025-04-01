package spot.spot.domain.job.query.util.caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.job.query.repository.dsl.SearchingListQueryDsl;
import static spot.spot.global.util.ConstantUtil.GEOHASH_PRECISION;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobGeoCacheSyncUtil {

    private final SearchingListQueryDsl searchingListQueryDsl;
    private final CacheManager cacheManager;
    private final GeoHashUtil geoHashUtil;


    @Transactional(readOnly = true)
    public void syncGeoHashCache () {
        int updateCnt = 0;
        Cache cache = cacheManager.getCache("job-geohash");

        List<NearByJobResponse> allResponses = searchingListQueryDsl.findJobsforGeoHashSync();
        Map<String, List<NearByJobResponse>> geoGrouped = new HashMap<>();

        // KEY: GEOHASH 문자열 값, VALUE: response dto 값
        for(NearByJobResponse response : allResponses) {
            String geoHash = geoHashUtil.encode(response.getLat(), response.getLng(), GEOHASH_PRECISION);
            geoGrouped.computeIfAbsent(geoHash, key -> new ArrayList<>()).add(response);
        }

        for(Map.Entry<String, List<NearByJobResponse>> entry : geoGrouped.entrySet()){
            String nowKey = entry.getKey();
            List<NearByJobResponse> dbList = entry.getValue();
            List<NearByJobResponse> cacheList = cache.get(nowKey, List.class);

            if(isChanged(cacheList, dbList)) {
                cache.put(nowKey, dbList);
                log.debug("[근처 일거리 찾기] {} 캐싱 갱신 {} 건", nowKey, dbList.size());
                updateCnt += dbList.size();
            }
            log.info("[GeoHashCache] 캐시 동기화 완료 - 총 {}개 갱신 / 전체 {}", updateCnt, geoGrouped.size());
        }
    }

    private boolean isChanged(List<NearByJobResponse> cacheList, List<NearByJobResponse> dbList) {
        if(cacheList == null || cacheList.size() != dbList.size()) return true;

        Map<Long, NearByJobResponse> dbListwithPK = dbList.stream()
            .collect(Collectors.toMap(NearByJobResponse::getId, j -> j));

        for(NearByJobResponse old : cacheList) {
            NearByJobResponse updated = dbListwithPK.get(old.getId());
            if(updated == null || !updated.equals(old)) return  true;
        }
        return false;
    }
}
