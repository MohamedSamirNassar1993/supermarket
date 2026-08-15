package com.supermarket.shared.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";
    public static final String ORG_HEADER = "X-Organization-Id";
    public static final String BRANCH_HEADER = "X-Branch-Id";

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI supermarketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Supermarket ERP API")
                        .description("""
                                REST API for Supermarket ERP platform covering inventory, sales, purchases,
                                expenses, HR/payroll, financial reporting, notifications, and dashboard analytics.
                                """)
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("Supermarket ERP Team")
                                .email("dev@supermarket.local"))
                        .license(new License().name("Proprietary")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local"),
                        new Server().url("/").description("Current host")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
