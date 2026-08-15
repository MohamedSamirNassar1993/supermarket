package com.supermarket.modules.inventory.application.service;

import com.supermarket.integration.catalog.RemoteProductCatalog;
import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.infrastructure.persistence.InventoryMovementRepository;
import com.supermarket.modules.inventory.infrastructure.persistence.OrganizationSettingsEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final StockBatchRepository stockBatchRepository;
    private final RemoteProductCatalog productCatalog;
    private final OrganizationSettingsService settingsService;
    private final InventoryMovementRepository inventoryMovementRepository;

    @Transactional(readOnly = true)
    public InventoryDtos.StockLevelResponse getStockLevel(UUID warehouseId, UUID productId, UUID variantId) {
        BigDecimal qty = stockBatchRepository.sumRemainingQuantity(warehouseId, productId, variantId);
        if (qty == null) {
            qty = BigDecimal.ZERO;
        }
        ProductEntity product = productCatalog.findById(productId);
        BigDecimal reorderLevel = product != null ? product.getReorderLevel() : null;
        boolean belowReorder = reorderLevel != null && qty.compareTo(reorderLevel) < 0;
        return InventoryDtos.StockLevelResponse.builder()
                .warehouseId(warehouseId)
                .productId(productId)
                .variantId(variantId)
                .quantityOnHand(qty)
                .reorderLevel(reorderLevel)
                .belowReorderLevel(belowReorder)
                .build();
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.LowStockAlertResponse> getLowStockAlerts(UUID organizationId) {
        OrganizationSettingsEntity settings = settingsService.getOrCreate(organizationId);
        if (!settings.isLowStockAlertEnabled()) {
            return List.of();
        }

        List<ProductEntity> products = productCatalog.findByOrganizationId(organizationId);
        List<InventoryDtos.LowStockAlertResponse> alerts = new ArrayList<>();

        for (ProductEntity product : products) {
            if (product.getReorderLevel() == null || !product.isTrackInventory()) {
                continue;
            }
            List<StockBatchEntity> batches = stockBatchRepository.findByOrganizationId(organizationId, Pageable.unpaged()).getContent();
            for (StockBatchEntity batch : batches) {
                if (!batch.getProductId().equals(product.getId())) {
                    continue;
                }
                BigDecimal qty = stockBatchRepository.sumRemainingQuantity(batch.getWarehouseId(), product.getId(), batch.getVariantId());
                if (qty != null && qty.compareTo(product.getReorderLevel()) < 0) {
                    alerts.add(InventoryDtos.LowStockAlertResponse.builder()
                            .productId(product.getId())
                            .variantId(batch.getVariantId())
                            .productName(product.getName())
                            .sku(product.getSku())
                            .quantityOnHand(qty)
                            .reorderLevel(product.getReorderLevel())
                            .warehouseId(batch.getWarehouseId())
                            .build());
                }
            }
        }
        return alerts;
    }

    @Transactional(readOnly = true)
    public List<InventoryDtos.ExpiringBatchResponse> getExpiringBatches(UUID organizationId, Integer days) {
        OrganizationSettingsEntity settings = settingsService.getOrCreate(organizationId);
        int alertDays = days != null ? days : settings.getExpiryAlertDays();
        LocalDate expiryBefore = LocalDate.now().plusDays(alertDays);

        return stockBatchRepository.findExpiringBatches(organizationId, expiryBefore).stream()
                .map(batch -> InventoryDtos.ExpiringBatchResponse.builder()
                        .batchId(batch.getId())
                        .warehouseId(batch.getWarehouseId())
                        .productId(batch.getProductId())
                        .variantId(batch.getVariantId())
                        .batchNumber(batch.getBatchNumber())
                        .remainingQuantity(batch.getRemainingQuantity())
                        .expiryDate(batch.getExpiryDate())
                        .daysUntilExpiry(ChronoUnit.DAYS.between(LocalDate.now(), batch.getExpiryDate()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<InventoryDtos.MovementResponse> listMovements(UUID organizationId, Pageable pageable) {
        return inventoryMovementRepository.findByOrganizationIdOrderByOccurredAtDesc(organizationId, pageable)
                .map(m -> InventoryDtos.MovementResponse.builder()
                        .id(m.getId())
                        .organizationId(m.getOrganizationId())
                        .warehouseId(m.getWarehouseId())
                        .productId(m.getProductId())
                        .variantId(m.getVariantId())
                        .movementType(m.getMovementType())
                        .quantity(m.getQuantity())
                        .unitCost(m.getUnitCost())
                        .totalCost(m.getTotalCost())
                        .referenceType(m.getReferenceType())
                        .referenceId(m.getReferenceId())
                        .notes(m.getNotes())
                        .occurredAt(m.getOccurredAt())
                        .build());
    }
}
