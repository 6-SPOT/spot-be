package spot.spot.global.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.FilterResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeninedHandler implements AccessDeniedHandler {

    private final FilterResponse filterResponse;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        filterResponse.error(response, accessDeniedException.getMessage());
        return;
    }
}
