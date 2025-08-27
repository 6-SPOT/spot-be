package spot.spot.global.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.repository.MemberRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import spot.spot.global.redis.service.TokenService;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.util.ConstantUtil;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    @Value("${spring.jwt.access.token}")
    private long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${spring.jwt.refresh.token}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtUtil(@Value("${spring.jwt.secretKey}") String secretKey, MemberRepository memberRepository,
        TokenService tokenService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.memberRepository = memberRepository;
        this.tokenService = tokenService;
    }

    public ErrorCode validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return null;
        } catch (ExpiredJwtException e) {
            return ErrorCode.EXPIRED_JWT;
        } catch (io.jsonwebtoken.security.SignatureException | SecurityException | MalformedJwtException e) {
            return ErrorCode.INVALID_JWT;
        } catch (UnsupportedJwtException e) {
            return ErrorCode.UNSUPPORTED_JWT;
        } catch (IllegalArgumentException e) {
            return ErrorCode.ILLEGAL_JWT;
        } catch (Exception e) {
            log.error("그 외의 에러가 났습니다. 에러 내용:", e);
            return ErrorCode.UNKNOWN_JWT_ERROR;
        }
    }

    //token생성
    public String createToken(OAuth2Member oAuth2Member, long expireTime){
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(oAuth2Member.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }
    // 개발 용 Token 생성
    public String createDeveloperToken(Member member) {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(member.getId().toString())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
            .signWith(secretKey)
            .compact();
    }
    //accessToken생성
    public String getAccessToken(OAuth2Member oAuth2Member){
        return createToken(oAuth2Member, ACCESS_TOKEN_EXPIRE_TIME);
    }
    //refreshToken생성
    public String getRefreshToken(OAuth2Member oAuth2Member) {
        return createToken(oAuth2Member, REFRESH_TOKEN_EXPIRE_TIME);
    }
    // Jwt 토큰 분해
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    //인증 객체 생성
    public Authentication createAuthentication(long id) {
        return memberRepository.findById(id)
            .map(OAuth2Member::new)
            .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
            .orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND.getMessage()) {});
    }

    public void setAuthentication (long id) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(id);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    //유효한 토큰인지 확인
    public Boolean isExpired(String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e){
            return true;
        }
    }

    public String separateBearer(String bearerToken) {
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(ConstantUtil.BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }

    // 만료된 토큰 재발급 -> REFRESH가 있으면 ACCESS 재발급, 없으면 에러 내야함.
    private void handleExpiredToken(String token, HttpServletRequest request, HttpServletResponse response) {

    }


}

