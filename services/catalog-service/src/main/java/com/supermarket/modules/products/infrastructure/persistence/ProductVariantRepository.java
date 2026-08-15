package com.supermarket.modules.products.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, UUID> {

    List<ProductVariantEntity> findByProductId(UUID productId);

    Optional<ProductVariantEntity> findByIdAndProductId(UUID id, UUID productId);

    boolean existsByProductIdAndSku(UUID productId, String sku);
}
