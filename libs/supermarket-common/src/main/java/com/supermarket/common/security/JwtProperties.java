package com.supermarket.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "supermarket.security.jwt")
public class JwtProperties {

    /**
     * PEM-encoded RSA public key used to verify JWT signatures issued by auth-service.
     */
    private String publicKey = "";
}
