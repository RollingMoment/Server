package com.RollinMoment.RollinMomentServer.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;


    private boolean isExemptPath(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 예외 처리할 경로들
        return path.equals("/")  // ✅ 홈 화면 추가
                || path.equals("/api/v1/auth/signUp")
                || path.equals("/api/v1/auth/signIn")
                || path.equals("/api/v1/auth/logout")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        if (isExemptPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 처리 중복 방지 플래그
        if (request.getAttribute("JwtFilterProcessed") != null) {
            log.warn("JwtAuthenticationFilter: 이미 처리된 요청입니다.");
            filterChain.doFilter(request, response);
            return;
        }
        request.setAttribute("JwtFilterProcessed", true);

        try {
            String accessToken = jwtTokenProvider.getHeaderToken(request, JwtTokenProvider.ACCESS_TOKEN_HEADER);
            //Access Token 유효시
            if (accessToken != null) {
                try {
                    if (jwtTokenProvider.validateToken(accessToken)) {
                        setAuthentication(accessToken);
                        log.info("AccessToken 인증 성공: {}", accessToken);
                    }
                    //Access Token 만료시
                } catch (ExpiredJwtException e) {
                    log.warn("Access Token 만료: {}", e.getMessage());
                    handleExpiredAccessToken(response);
                    return;
                }
            } else {
                log.info("Access Token 없음. Refresh Token 확인 시작");
                handleExpiredAccessToken(response);
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Unhandled exception in JwtFilter: {}", e.getMessage(), e);
            if (!response.isCommitted()) {
                jwtUtil.jwtExceptionHandler(response, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }


    private void handleExpiredAccessToken(HttpServletResponse response) throws IOException {
        log.error("Access Token 만료 - 로그인 필요");
        jwtUtil.jwtExceptionHandler(response, "Access Token 만료. 다시 로그인 필요.", HttpStatus.UNAUTHORIZED);
    }



    private void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication 설정: {}", authentication.getName());
    }
}