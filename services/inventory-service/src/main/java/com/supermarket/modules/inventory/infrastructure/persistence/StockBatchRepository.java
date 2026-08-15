package com.supermarket.modules.inventory.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockBatchRepository extends JpaRepository<StockBatchEntity, UUID> {

    Page<StockBatchEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Page<StockBatchEntity> findByWarehouseId(UUID warehouseId, Pageable pageable);

    Optional<StockBatchEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("""
            SELECT b FROM StockBatchEntity b
            WHERE b.warehouseId = :warehouseId
              AND b.productId = :productId
              AND (:variantId IS NULL AND b.variantId IS NULL OR b.variantId = :variantId)
              AND b.remainingQuantity > 0
              AND b.active = true
            ORDER BY b.receivedAt ASC
            """)
    List<StockBatchEntity> findAvailableBatches(
            @Param("warehouseId") UUID warehouseId,
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId);

    @Query("""
            SELECT COALESCE(SUM(b.remainingQuantity), 0) FROM StockBatchEntity b
            WHERE b.warehouseId = :warehouseId
              AND b.productId = :productId
              AND (:variantId IS NULL AND b.variantId IS NULL OR b.variantId = :variantId)
              AND b.active = true
            """)
    java.math.BigDecimal sumRemainingQuantity(
            @Param("warehouseId") UUID warehouseId,
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId);

    @Query("""
            SELECT b FROM StockBatchEntity b
            WHERE b.organizationId = :organizationId
              AND b.expiryDate IS NOT NULL
              AND b.expiryDate <= :expiryBefore
              AND b.remainingQuantity > 0
              AND b.active = true
            ORDER BY b.expiryDate ASC
            """)
    List<StockBatchEntity> findExpiringBatches(
            @Param("organizationId") UUID organizationId,
            @Param("expiryBefore") LocalDate expiryBefore);
}
