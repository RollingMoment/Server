package com.RollinMoment.RollinMomentServer.jwt;


import com.RollinMoment.RollinMomentServer.config.JwtProperties;
import com.RollinMoment.RollinMomentServer.member.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000L; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 7일
    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final CustomUserDetailsService customUserDetailsService;


    @PostConstruct
    protected void init() {
        log.info("초기화된 비밀 키: {}", jwtProperties.getSecret()); // ✅ 올바르게 값 출력
    }

    // Access Token 생성
    public String generateAccessToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId); // ✅ userId만 JWT에 포함
        Date now = new Date();
        log.info("생성된 Access Token 생성 : {}", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public Date getRefreshTokenExpiryDate() {
        return new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION);
    }


    // JWT 검증 및 인증 객체 반환
    public Authentication getAuthentication(String token) {
        // JWT Claims 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 권한 정보 추출 및 변환
        List<GrantedAuthority> authorities = extractAuthorities(claims);
        String userId = claims.getSubject();
        UserDetails securityUser = customUserDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(securityUser, null, authorities);
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN_HEADER, BEARER_PREFIX + accessToken);
    }


    public String getHeaderToken(HttpServletRequest request, String tokenHeader) {
        String bearerToken = request.getHeader(tokenHeader);
        log.info("Authorization 헤더 값: {}", bearerToken != null ? bearerToken : "Authorization 헤더가 존재하지 않음");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (bearerToken == null) {
            log.error("Authorization 헤더가 요청에 포함되지 않았습니다.");
        } else {
            log.error("Authorization 헤더가 'Bearer '로 시작하지 않습니다.");
        }
        return null;
    }


    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        return Optional.ofNullable(claims.get("roles"))
                .map(auth -> Arrays.stream(auth.toString().split(","))
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.trim()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());


    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtProperties.getSecret()).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw e; // Access Token 만료시 예외 발생
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    public String stripBearerPrefix(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
