package com.supermarket.modules.inventory.application.dto;

import com.supermarket.modules.inventory.domain.MovementType;
import com.supermarket.modules.inventory.domain.ValuationMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class InventoryDtos {

    private InventoryDtos() {
    }

    @Getter
    @Setter
    public static class SettingsUpdateRequest {
        private ValuationMethod valuationMethod;
        private Boolean allowNegativeStock;
        private Boolean lowStockAlertEnabled;
        private Integer expiryAlertDays;
    }

    @Getter
    @Builder
    public static class SettingsResponse {
        private UUID id;
        private UUID organizationId;
        private ValuationMethod valuationMethod;
        private boolean allowNegativeStock;
        private boolean lowStockAlertEnabled;
        private int expiryAlertDays;
    }

    @Getter
    @Setter
    public static class StockBatchCreateRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID warehouseId;
        @NotNull
        private UUID productId;
        private UUID variantId;
        @NotBlank @Size(max = 100)
        private String batchNumber;
        @NotNull @DecimalMin("0.0001")
        private BigDecimal quantity;
        @NotNull @DecimalMin("0")
        private BigDecimal unitCost;
        private LocalDate expiryDate;
        private String sourceType;
        private UUID sourceId;
    }

    @Getter
    @Builder
    public static class StockBatchResponse {
        private UUID id;
        private UUID organizationId;
        private UUID warehouseId;
        private UUID productId;
        private UUID variantId;
        private String batchNumber;
        private BigDecimal quantity;
        private BigDecimal remainingQuantity;
        private BigDecimal unitCost;
        private Instant receivedAt;
        private LocalDate expiryDate;
        private String sourceType;
        private UUID sourceId;
        private boolean active;
    }

    @Getter
    @Setter
    public static class StockInRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID warehouseId;
        @NotNull
        private UUID productId;
        private UUID variantId;
        @NotNull @DecimalMin("0.0001")
        private BigDecimal quantity;
        @NotNull @DecimalMin("0")
        private BigDecimal unitCost;
        private String batchNumber;
        private LocalDate expiryDate;
        private String notes;
    }

    @Getter
    @Setter
    public static class StockOutRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID warehouseId;
        @NotNull
        private UUID productId;
        private UUID variantId;
        @NotNull @DecimalMin("0.0001")
        private BigDecimal quantity;
        private String notes;
    }

    @Getter
    @Builder
    public static class MovementResponse {
        private UUID id;
        private UUID batchId;
        private UUID organizationId;
        private UUID warehouseId;
        private UUID productId;
        private UUID variantId;
        private MovementType movementType;
        private BigDecimal quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private String referenceType;
        private UUID referenceId;
        private String notes;
        private Instant occurredAt;
        private List<AllocationResponse> allocations;
    }

    @Getter
    @Builder
    public static class AllocationResponse {
        private UUID batchId;
        private BigDecimal quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
    }

    @Getter
    @Builder
    public static class StockLevelResponse {
        private UUID warehouseId;
        private UUID productId;
        private UUID variantId;
        private BigDecimal quantityOnHand;
        private BigDecimal reorderLevel;
        private boolean belowReorderLevel;
    }

    @Getter
    @Builder
    public static class LowStockAlertResponse {
        private UUID productId;
        private UUID variantId;
        private String productName;
        private String sku;
        private BigDecimal quantityOnHand;
        private BigDecimal reorderLevel;
        private UUID warehouseId;
    }

    @Getter
    @Builder
    public static class ExpiringBatchResponse {
        private UUID batchId;
        private UUID warehouseId;
        private UUID productId;
        private UUID variantId;
        private String batchNumber;
        private BigDecimal remainingQuantity;
        private LocalDate expiryDate;
        private long daysUntilExpiry;
    }
}
