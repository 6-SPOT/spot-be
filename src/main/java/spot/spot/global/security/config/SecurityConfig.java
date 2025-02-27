package spot.spot.global.security.config;

import jakarta.servlet.DispatcherType;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import spot.spot.domain.member.service.*;
import spot.spot.global.redis.service.TokenService;
import spot.spot.global.response.format.FilterResponse;
import spot.spot.global.security.filter.CustomAccessDeninedHandler;
import spot.spot.global.security.filter.CustomAuthenticationEntryPoint;
import spot.spot.global.security.filter.JwtFilter;
import spot.spot.global.security.filter.OAuth2FailureHandler;
import spot.spot.global.security.filter.OAuth2SuccessHandler;
import spot.spot.global.security.util.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /*
     *  Security Config의 변수 설명 - 필요한 것
     *  (1) JwtUtil                     : Jwt 토큰 발급, 검증 등의 로직 담당
     *  (2) FilterResponse              : SpringMVC를 거치지 않고 바로 에러 반환을 위한 DTO 객체
     *  (3) OAtuth2MemberService        : Oauth2-Client 이용 처리 로직
     *  (4) Oauth2SuccessHandler        : Oauth2 인증 서버와 통신 성공 시 사용되는 Handler
     *  (5) Oauth2FailureHandler        : Oauth2 인증 서버와 통신 실패 시 사용되는 Handler
     *  (6) AuthenticationEntryPoint    : 인증 실패 시 예외 처리
     *  (7) AccessDeniedHandler         : 해당 경로를 요청할 권한이 없을 때 예외 처리
     *  (8) White List                  : 모든 유적 권한 제약 없이 요청 가능한 페이지
     */

    private final JwtUtil jwtUtil;
    private final FilterResponse filterResponse;
    private final OAuth2MemberService oAuth2MemberService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeninedHandler customAccessDeninedHandler;

    private final String[] whiteList = {
        "/ws-stomp/**", // * 웹 소켓 연결 및 테스팅이 완료되면 삭제
        "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/swagger-resources/**",
        "/webjars/**", "/error",
    };



    /*
     * Security Filter Chain => 해당 Bean은 Delegating Filter Proxy에 의해 Servlet 레벨에서 동작한다.
     * -------------------------------------------------------------------------------------------
     * (1) API 서버를 만들고 있음으로, 인증 안된 사용자에게 로그인 화면을 Redirect 하는 formLogin 비활성화
     * (2) 마찬가지로 유효한 Basic 토큰이 없으면 인증 화면을 띄우는 httpBasic 도 비활성화
     * (3) API 라서 사이트 위조 공격이 들어오지 않는다. -> csrf 도 비활성화
     * (4) 다른 출처도 우리 리소스를 쓸 수 있게 설정 (같은 출처 = 프로토콜, 호스트, 포트 동일)
     * (5) iframe 설정은 동일 출처에게만 허락한다.
     * (6) 로그아웃 설정 -> [yaml 파일에 정한 Context root]는 빼줘야 한다.
     * (6) Session 을 StateLess 하게 바꾼다. JWT 기반은 Session 을 요청 단위로 쓰고 죽인다.
     * (7) 권한 설정
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .formLogin(AbstractHttpConfigurer::disable);       // (1)
        http
            .httpBasic(AbstractHttpConfigurer::disable);       // (2)
        http
            .csrf(AbstractHttpConfigurer::disable);            // (3)
        http
            .cors((corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource())));       // (4)
        http
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));    // (5)
        http
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );                                                  // (7)
        http
            .authorizeHttpRequests(
                auth ->
                    auth                                        // (8)
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()   // 비동기 접근 열어주기
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() // FORWARD REDIRECTING 열어주기
                        .requestMatchers("/api/member/login/kakao", "/api/login/oauth2/code/kakao", "/api/**").permitAll()
                        .requestMatchers("/api/swagger-ui.html", "/api/swagger-ui/**", "/v3/api-docs/**", "/api/swagger-resources/**").permitAll()
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(login -> login
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/member/login"))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2MemberService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler))
                .addFilterAt(new JwtFilter(jwtUtil, filterResponse), UsernamePasswordAuthenticationFilter.class);;
        http
                .exceptionHandling(auth ->
                    auth.authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeninedHandler));
        return http.build();
    }

    @Bean
        // CORS 설정
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
            List.of("*"));
        configuration.addAllowedMethod(CorsConfiguration.ALL); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader(CorsConfiguration.ALL); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 구성 적용
        return source;
    }
}
