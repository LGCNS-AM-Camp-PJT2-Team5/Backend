package com.team9.jobbotdari.security;

import java.io.IOException;
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

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final List<String> EXCLUDE_PATHS = List.of(
            "/swagger-ui", "/swagger-ui.html", "/v3/api-docs", "/swagger-resources", "/webjars",
            "/error", "/signin", "/signup", "/api/auth", "/api/files"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String role = request.getHeader("X-Role");

        log.info("요청 정보: userId={}, username={}, role={}, uri={}", userId, username, role, request.getRequestURI());

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        return EXCLUDE_PATHS.stream().anyMatch(uri::startsWith);
    }
}