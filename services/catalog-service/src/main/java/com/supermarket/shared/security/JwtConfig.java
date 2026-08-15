package com.supermarket.shared.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Shared JWT signing key bean for components that inject {@link SecretKey} directly.
 * Token generation and validation is handled by {@code JwtService} in the auth module.
 */
@Configuration
public class JwtConfig {

    @Value("${supermarket.security.jwt.secret:change-me-to-a-secure-secret-key-at-least-256-bits-long-for-hs256}")
    private String jwtSecret;

    @Bean
    public SecretKey jwtSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
