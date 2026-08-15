package com.supermarket.modules.products.application;

import com.supermarket.integration.catalog.CatalogFeignClient;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class ProductLookupService {

    private final CatalogFeignClient catalogFeignClient;

    public ProductEntity requireProduct(UUID organizationId, UUID productId) {
        var response = catalogFeignClient.getProduct(productId, organizationId);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Product not found: " + productId);
        }
        ProductEntity product = response.getData();
        if (!product.isActive()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Product not found: " + productId);
        }
        return product;
    }
}
