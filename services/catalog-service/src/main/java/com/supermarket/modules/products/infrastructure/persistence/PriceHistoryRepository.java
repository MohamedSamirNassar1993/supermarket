package com.supermarket.modules.products.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceHistoryRepository extends JpaRepository<PriceHistoryEntity, UUID> {

    Page<PriceHistoryEntity> findByProductIdOrderByEffectiveAtDesc(UUID productId, Pageable pageable);

    Page<PriceHistoryEntity> findByProductIdAndVariantIdOrderByEffectiveAtDesc(UUID productId, UUID variantId, Pageable pageable);
}
