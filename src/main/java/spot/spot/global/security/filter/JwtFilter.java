package spot.spot.global.security.filter;

import static spot.spot.global.util.ConstantUtil.AUTHORIZATION;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import spot.spot.domain.member.service.MemberService;
import spot.spot.global.redis.service.TokenService;

import java.io.IOException;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.FilterResponse;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.JwtUtil;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final FilterResponse filterResponse;


    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.separateBearer(request.getHeader(AUTHORIZATION));
        // 유효성 검사
        ErrorCode error  = jwtUtil.validateToken(token);
        if(error != null) {
            filterResponse.error(response, error);
            return;
        }

        // 인증 객체 설정
        log.info("✅ 인증 성공: 필터 체인 실행");
        Claims userInfo = jwtUtil.getUserInfoFromToken(token);
        jwtUtil.setAuthentication(Long.parseLong(userInfo.getSubject()));


        filterChain.doFilter(request, response);
    }
}
