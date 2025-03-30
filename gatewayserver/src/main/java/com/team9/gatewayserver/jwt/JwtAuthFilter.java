package com.team9.gatewayserver.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter {

    private final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 인증 제외 경로
        if (path.startsWith("/api/auth") ||
                path.startsWith("/api/files") ||
                path.startsWith("/api/recruitment") ||
                path.startsWith("/api/company")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());

        if (token != null && jwtUtils.isTokenValid(token)) {
            Claims claims = jwtUtils.getAllClaimsFromToken(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.get("userId").toString())
                    .header("X-Username", claims.get("username").toString())
                    .header("X-Role", claims.get("role").toString())
                    .build();

            exchange = exchange.mutate().request(mutatedRequest).build();
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private String extractToken(ServerHttpRequest request) {
        String bearer = request.getHeaders().getFirst("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
