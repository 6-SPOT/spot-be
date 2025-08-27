package spot.spot.global.response.handler;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

public class ResponseStatusSetter {
    public static void set(HttpStatus status) {
        if(status.isError()) throw new GlobalException(ErrorCode.NOT_ALLOW_STATUS_SETTER_4_ERROR);
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attrs.getResponse();
        response.setStatus(status.value());
    }
}
