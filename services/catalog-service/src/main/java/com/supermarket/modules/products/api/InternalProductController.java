package com.supermarket.modules.products.api;

import com.supermarket.modules.products.application.ProductLookupService;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductRepository;
import com.supermarket.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/internal")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductLookupService productLookupService;
    private final ProductRepository productRepository;

    @GetMapping("/{productId}")
    public ApiResponse<ProductEntity> getProduct(
            @PathVariable UUID productId,
            @RequestParam UUID organizationId) {
        return ApiResponse.success(productLookupService.requireProduct(organizationId, productId));
    }

    @GetMapping
    public ApiResponse<List<ProductEntity>> listProducts(@RequestParam UUID organizationId) {
        return ApiResponse.success(
                productRepository.findByOrganizationId(organizationId, Pageable.unpaged()).getContent());
    }
}
