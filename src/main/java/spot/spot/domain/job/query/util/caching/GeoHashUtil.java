package spot.spot.domain.job.query.util.caching;

import ch.hsr.geohash.GeoHash;
import org.springframework.stereotype.Component;

@Component
public class GeoHashUtil {
    // 위도 경도를 Geo-hash 문자열로 인코딩 -> Redis caching 시 Key로 쓰인다.
    // precision: 정밀도, Geo-hash는 격자 기반, 여기서 precision 값이 커지면 검색 범위 격자가 좁아지고, 작아지면 검색 범위 격자가 넓어진다.
    public String encode (double lat, double lng, int precision) {
        return GeoHash.geoHashStringWithCharacterPrecision(lat, lng, precision);
    }
}
