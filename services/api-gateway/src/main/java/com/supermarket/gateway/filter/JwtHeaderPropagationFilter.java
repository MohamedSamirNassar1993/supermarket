package com.supermarket.gateway.filter;

import com.supermarket.common.security.JwtTokenValidator;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtHeaderPropagationFilter implements GlobalFilter, Ordered {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLES = "X-User-Roles";

    private final ObjectProvider<JwtTokenValidator> jwtTokenValidator;

    public JwtHeaderPropagationFilter(ObjectProvider<JwtTokenValidator> jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        JwtTokenValidator validator = jwtTokenValidator.getIfAvailable();
        if (validator == null) {
            return chain.filter(exchange);
        }

        String token = authorization.substring(7);
        if (!validator.isValid(token)) {
            return chain.filter(exchange);
        }

        Claims claims = validator.parse(token);
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(HEADER_USER_ID, validator.getUserId(claims).toString())
                .header(HEADER_USER_ROLES, String.join(",", validator.getRoles(claims)))
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
