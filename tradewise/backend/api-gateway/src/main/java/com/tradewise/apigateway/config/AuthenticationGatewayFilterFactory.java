package com.tradewise.apigateway.config;

import com.tradewise.apigateway.service.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    public AuthenticationGatewayFilterFactory(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                ServerHttpRequest request = exchange.getRequest();
                String path = request.getURI().getPath();

                // Public auth endpoints
                if (path.startsWith("/api/auth")) {
                    return chain.filter(exchange);
                }

                String jwt = extractJwt(request, path);

                if (jwt == null || !jwtUtil.validateToken(jwt)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                String email = jwtUtil.extractEmail(jwt);
                if (email == null || email.isBlank()) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Email", email)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception ex) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private String extractJwt(ServerHttpRequest request, String path) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (path.startsWith("/ws")) {
            return request.getQueryParams().getFirst("token");
        }

        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}