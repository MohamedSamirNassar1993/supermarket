package com.supermarket.integration.catalog;

import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoteProductCatalog {

    private final CatalogFeignClient catalogFeignClient;

    public ProductEntity findById(UUID productId) {
        return findById(null, productId);
    }

    public ProductEntity findById(UUID organizationId, UUID productId) {
        var response = catalogFeignClient.getProduct(productId, organizationId);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            return null;
        }
        return response.getData();
    }

    public List<ProductEntity> findByOrganizationId(UUID organizationId) {
        var response = catalogFeignClient.listProducts(organizationId);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Catalog service call failed");
        }
        return response.getData();
    }
}
