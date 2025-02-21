package com.team9.jobbotdari.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.team9.jobbotdari.common.jwt.JwtUtils;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    // Swagger 및 인증 없이 허용할 경로
    private final List<String> EXCLUDE_PATHS = List.of(
            "/swagger-ui", "/swagger-ui.html", "/v3/api-docs", "/swagger-resources", "/webjars",
            "/error", "/signin", "/signup", "/api/auth"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtAuthenticationFilter 실행 ...");

        String jwtToken = null;
        String subject = null;

        try {

            // Authorization 헤더 포함 여부를 확인하고, 헤더 정보를 추출
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);
                subject = jwtUtils.getSubjectFromToken(jwtToken);
            } else {
                log.warn("Authorization 헤더 누락 또는 토큰 형식 오류");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token: Authorization 헤더가 없거나 올바르지 않은 형식");
                response.getWriter().flush();
                return;
            }

            // 유저 정보 조회
            User user = userRepository.findByUsername(subject);
            if (user == null) {
                log.warn("유효하지 않은 사용자: {}", subject);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token: User not found");
                response.getWriter().flush();
                return;
            }

            // JWT 검증
            if (!jwtUtils.validateToken(jwtToken, user)) {
                log.warn("JWT 검증 실패: {}", subject);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                response.getWriter().flush();
                return;
            }

            // UserDetails 로드 및 SecurityContext 설정
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(subject);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            log.info("사용자 인증 성공: {}", subject);

        } catch (Exception e) {
            log.error("JWT 처리 중 예외 발생: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT Token: " + e.getMessage());
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.debug("shouldNotFilter 실행 ...");

        String uri = request.getRequestURI();
        boolean isExcluded = EXCLUDE_PATHS.stream().anyMatch(uri::startsWith);
        log.debug("URI {} 제외 여부: {}", uri, isExcluded);
        return isExcluded;
    }
}