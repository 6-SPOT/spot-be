package spot.spot.global.util;

public class ConstantUtil {
    // FOR SECURITY
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_PREFIX = "token:";
    // 거리 계산
    public static final double EARTH_RADIUS_KM = 6371;
    // WORKER STAUTS
    public static final Integer STILL_WORKING   = 0;
    public static final Integer LITTLE_BREAK    = 1;
    // MESSAGE TYPE
    public static final String TYPE = "type";
    public static final String PERMIT_ALL = "permitAll";
    public static final String AUTH_ERROR = "auth_error";
}
