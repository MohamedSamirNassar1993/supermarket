package com.supermarket.integration.catalog;

import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.shared.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogFeignClient {

    @GetMapping("/api/v1/products/internal/{productId}")
    ApiResponse<ProductEntity> getProduct(
            @PathVariable("productId") UUID productId,
            @RequestParam("organizationId") UUID organizationId);
}
