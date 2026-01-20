package com.tradewise.apigateway.config;

import com.tradewise.apigateway.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationGatewayFilterFactory(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // 1. Check if the path is public (e.g., /api/auth/**)
            if (path.contains("/api/auth")) {
                return chain.filter(exchange); // Let it pass
            }

            String jwt = null;

            // 2. Try to get JWT from Authorization header
            if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                }
            }

            // 3. If no JWT from header, and it's a WebSocket path, try to get from query parameter
            if (jwt == null && path.contains("/ws")) {
                jwt = request.getQueryParams().getFirst("token");
            }

            // 4. If no JWT found, or if invalid, return UNAUTHORIZED
            if (jwt == null || !jwtUtil.validateToken(jwt)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // 5. Add the X-User-Email header to the request
            String email = jwtUtil.extractEmail(jwt);
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Email", email)
                    .build();
            
            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Empty config class
    }
}
