package spot.spot.global.util;

public class ConstantUtil {
    // FOR SECURITY
    public static final String AUTHORIZATION = "Authorization";
    // JOB STATUS
    public static final Integer OWNER           = 0;
    public static final Integer ATTENDER        = 1;
    public static final Integer WORKER          = 2;
    public static final Integer CANCEL_REQUEST  = 3;
    public static final Integer CANCEL_COMPLETE = 4;
    // WORKER STAUTS
    public static final Integer STILL_WORKING   = 0;
    public static final Integer LITTLE_BREAK    = 1;
}
