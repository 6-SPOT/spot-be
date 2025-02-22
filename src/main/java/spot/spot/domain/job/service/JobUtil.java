package spot.spot.domain.job.service;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import spot.spot.global.util.ConstantUtil;

@Component
public class JobUtil {

    // 줌 레벨을 실제 KM로 변환하는 함수
    public double convertZoomToRadius(int zoom_level) {
        return switch (zoom_level) {
            case 21 -> 0.05;
            case 20 -> 0.1;
            case 19 -> 0.2;
            case 18 -> 0.5;
            case 17 -> 1;
            case 16 -> 2;
            case 15 -> 5;
            case 14 -> 10;
            case 13 -> 20;
            case 12 -> 50;
            default -> 100;
        };
    }

    // 위도 경도 간의 차이를 km 차이로 변환하는 함수
    @Named("haversineDistance")
    public static double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double distance_ratio = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double distance_radian = 2 * Math.atan2(Math.sqrt(distance_ratio), Math.sqrt(1 - distance_ratio));

        return ConstantUtil.EARTH_RADIUS_KM * distance_radian;
    }
}
