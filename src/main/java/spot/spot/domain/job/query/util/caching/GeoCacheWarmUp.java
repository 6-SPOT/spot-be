package spot.spot.domain.job.query.util.caching;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoCacheWarmUp {

    private final JobGeoCacheSyncUtil jobGeoCacheSyncUtil;

    @PostConstruct
    public void warmUpGeoCacheOnStart() {
      log.info("[GeoHashCache] 서버 부팅에 따른 동기화 시작");
      jobGeoCacheSyncUtil.syncGeoHashCache();
    }
}
