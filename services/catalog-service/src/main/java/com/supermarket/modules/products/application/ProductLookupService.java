package com.supermarket.modules.products.application;

import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductRepository;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductLookupService {

    private final ProductRepository productRepository;

    public ProductEntity requireProduct(UUID organizationId, UUID productId) {
        return productRepository.findByIdAndOrganizationId(productId, organizationId)
                .filter(ProductEntity::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Product not found: " + productId));
    }
}
