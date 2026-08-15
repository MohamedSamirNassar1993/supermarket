package com.supermarket.gateway.config;

import com.supermarket.common.security.JwtProperties;
import com.supermarket.common.security.JwtTokenValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class GatewaySecurityConfig {

    @Bean
    @ConditionalOnExpression("!'${supermarket.security.jwt.public-key:}'.isBlank()")
    JwtTokenValidator jwtTokenValidator(JwtProperties jwtProperties) {
        return new JwtTokenValidator(jwtProperties);
    }
}
