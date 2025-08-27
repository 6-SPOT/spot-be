package spot.spot.global.response.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import spot.spot.global.logging.ColorLogger;

@Component
public class FilterResponse {

    private final ObjectMapper mapper = new ObjectMapper();

    // Servlet Filter 단계에서 정상 종료 하고 바로 값 반환
    public <T> HttpServletResponse ok(HttpServletResponse response, T data) throws IOException {

        HashMap<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("message", "성공하였습니다.");
        responseBody.put("data", data);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(mapper.writeValueAsString(responseBody));

        return response;
    }

    // Servlet Filter 단계에서 에러 반환
    public  void error(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ColorLogger.red(errorCode.getMessage());
        HashMap<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "fail");
        responseBody.put("message",errorCode.getMessage());
        responseBody.put("data", null);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }

    public void error(HttpServletResponse response, String msg) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
    }
}
