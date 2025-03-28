package spot.spot.global.security.filter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.FilterResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final FilterResponse filterResponse;

    @PostConstruct
    public void init() {
        log.info("✅ CustomAuthenticationEntryPoint가 SecurityConfig에 등록됨");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getMessage());
        filterResponse.error(response, authException.getMessage());
        return;
    }
}
