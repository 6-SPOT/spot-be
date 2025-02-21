package spot.spot.domain.member.entity.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.OAuth2Member;
import spot.spot.domain.member.service.MemberService;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final MemberService memberService;

    @Value("${spring.jwt.access.token}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${spring.jwt.refresh.token}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtUtil(@Value("${spring.jwt.secretKey}") String secretKey, MemberService memberService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.memberService =memberService;
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

    //accessToken생성
    public String getAccessToken(OAuth2Member oAuth2Member){
        return createToken(oAuth2Member, ACCESS_TOKEN_EXPIRE_TIME);
    }

    //refreshToken생성
    public String getRefreshToken(OAuth2Member oAuth2Member) {
        return createToken(oAuth2Member, REFRESH_TOKEN_EXPIRE_TIME);
    }

    //token으로 유저 loginId조회
    public String getLoginId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //인증 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // ✅ JWT의 subject를 Long 타입의 member ID로 변환하여 사용자 조회
        Long memberId = Long.parseLong(claims.getSubject());
        Member findMember = memberService.findById(memberId);
        if (findMember == null) {
            throw new UsernameNotFoundException("일치하는 유저가 없습니다.");
        }

        OAuth2Member userDetails = new OAuth2Member(findMember);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
}

