package spot.spot.global.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.global.redis.service.TokenService;
import spot.spot.global.redis.entity.Token;

import java.io.IOException;
import java.net.URLEncoder;
import spot.spot.global.security.util.JwtUtil;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2Member oAuth2Member = (OAuth2Member) authentication.getPrincipal();

        String memberId = oAuth2Member.getName();
        String nickname = oAuth2Member.getNickName();

        String accessToken = jwtUtil.getAccessToken(oAuth2Member);
        String refreshToken = jwtUtil.getRefreshToken(oAuth2Member);
        Token token = Token.builder().accessToken(accessToken).refreshToken(refreshToken).memberId(memberId).build();
        tokenService.saveToken(token);

        String encodedNickname = nickname.matches("^[a-zA-Z0-9]*$") ? nickname : URLEncoder.encode(nickname, "UTF-8");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(oAuth2Member, null, oAuth2Member.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String redirectUri = request.getRequestURL().toString();  // 요청된 전체 URL

        String redirectUrl = "https://ilmatch.net/oauth2/redirect";

//        if (redirectUri.contains("localhost:8080")) {
//            redirectUrl = "https://ilmatch.net/oauth2/redirect";
//        } else if (redirectUri.contains("ilmatch.net")) {
//            redirectUrl = "http://localhost:3000/oauth2/redirect";
//        }

        if (redirectUri.contains("localhost:8080")) {
            redirectUrl = "http://localhost:3000/oauth2/redirect";
        } else if (redirectUri.contains("ilmatch.net")) {
            redirectUrl = "https://ilmatch.net/oauth2/redirect";
        }
        // 🛠 리다이렉트 URL 설정
        response.sendRedirect(redirectUrl + "?accessToken=" + accessToken + "&refreshToken=" + refreshToken + "&nickname=" + encodedNickname);

    }
}
