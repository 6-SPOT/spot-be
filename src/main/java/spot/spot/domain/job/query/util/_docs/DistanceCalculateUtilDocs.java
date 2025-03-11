package spot.spot.domain.job.query.util._docs;

import org.mapstruct.Named;
import spot.spot.global.util.ConstantUtil;

public interface DistanceCalculateUtilDocs {
    // 1) from zoom-level to KM
    public double convertZoomToRadius(int zoom_level);

    // 2) from lat,lng to KM
    @Named("haversineDistance")
    public static double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2){
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double distance_ratio = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double distance_radian = 2 * Math.atan2(Math.sqrt(distance_ratio), Math.sqrt(1 - distance_ratio));

        return ConstantUtil.EARTH_RADIUS_KM * distance_radian;
    }

}
