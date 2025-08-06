package hackathon.bigone.sunsak.global.security.jwt;

import hackathon.bigone.sunsak.global.security.jwt.dto.JwtTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final long ACCESS_TOKEN_VALID_TIME = 1000 * 60 * 30; // 30분
    private final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey); // 👈 Base64 디코딩 추가
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public JwtTokenDto createToken(String username) {
        Date now = new Date();
        Date accessExpires = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME); //만료일
        Date refreshExpires = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);

        String accessToken = Jwts.builder() //accessToken 생성
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(accessExpires)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder() //refreshToken 생성
                .setExpiration(refreshExpires)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenDto.builder() //응답 보여주기
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String token) {
        String username = getUserPk(token);
        var userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserPk(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRemainingExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key) // 시크릿 키
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 이후의 실제 토큰만 잘라서 반환
        }

        return null;  // 없거나 형식 안 맞으면 null 반환
    }
}

