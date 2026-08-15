package com.supermarket.modules.warehouse.application.dto;

import com.supermarket.modules.warehouse.domain.AdjustmentStatus;
import com.supermarket.modules.warehouse.domain.StockCountStatus;
import com.supermarket.modules.warehouse.domain.TransferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class WarehouseDtos {

    private WarehouseDtos() {
    }

    @Getter
    @Setter
    public static class WarehouseCreateRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID branchId;
        @NotBlank @Size(max = 50)
        private String code;
        @NotBlank @Size(max = 255)
        private String name;
        private String address;
        private String warehouseType;
    }

    @Getter
    @Setter
    public static class WarehouseUpdateRequest {
        @Size(max = 255)
        private String name;
        private String address;
        private String warehouseType;
        private Boolean active;
    }

    @Getter
    @Builder
    public static class WarehouseResponse {
        private UUID id;
        private UUID organizationId;
        private UUID branchId;
        private String code;
        private String name;
        private String address;
        private String warehouseType;
        private boolean active;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Getter
    @Setter
    public static class TransferLineRequest {
        @NotNull
        private UUID productId;
        private UUID variantId;
        @NotNull @DecimalMin("0.0001")
        private BigDecimal quantity;
    }

    @Getter
    @Setter
    public static class TransferCreateRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID fromWarehouseId;
        @NotNull
        private UUID toWarehouseId;
        @NotBlank @Size(max = 50)
        private String transferNumber;
        private String notes;
        @NotEmpty
        private List<TransferLineRequest> lines;
    }

    @Getter
    @Builder
    public static class TransferLineResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private BigDecimal quantity;
        private BigDecimal receivedQuantity;
    }

    @Getter
    @Builder
    public static class TransferResponse {
        private UUID id;
        private UUID organizationId;
        private UUID fromWarehouseId;
        private UUID toWarehouseId;
        private String transferNumber;
        private TransferStatus status;
        private String notes;
        private Instant shippedAt;
        private Instant receivedAt;
        private List<TransferLineResponse> lines;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Getter
    @Setter
    public static class StockCountLineRequest {
        @NotNull
        private UUID productId;
        private UUID variantId;
        @NotNull @DecimalMin("0")
        private BigDecimal systemQuantity;
        private BigDecimal countedQuantity;
    }

    @Getter
    @Setter
    public static class StockCountCreateRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID warehouseId;
        @NotBlank @Size(max = 50)
        private String countNumber;
        private String notes;
        @NotEmpty
        private List<StockCountLineRequest> lines;
    }

    @Getter
    @Builder
    public static class StockCountLineResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private BigDecimal systemQuantity;
        private BigDecimal countedQuantity;
        private BigDecimal variance;
    }

    @Getter
    @Builder
    public static class StockCountResponse {
        private UUID id;
        private UUID organizationId;
        private UUID warehouseId;
        private String countNumber;
        private StockCountStatus status;
        private String notes;
        private Instant countedAt;
        private Instant completedAt;
        private List<StockCountLineResponse> lines;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Getter
    @Setter
    public static class AdjustmentLineRequest {
        @NotNull
        private UUID productId;
        private UUID variantId;
        @NotNull
        private BigDecimal quantityChange;
        private BigDecimal unitCost;
        private String notes;
    }

    @Getter
    @Setter
    public static class AdjustmentCreateRequest {
        @NotNull
        private UUID organizationId;
        @NotNull
        private UUID warehouseId;
        @NotBlank @Size(max = 50)
        private String adjustmentNumber;
        @NotBlank
        private String reason;
        private String notes;
        @NotEmpty
        private List<AdjustmentLineRequest> lines;
    }

    @Getter
    @Builder
    public static class AdjustmentLineResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private BigDecimal quantityChange;
        private BigDecimal unitCost;
        private String notes;
    }

    @Getter
    @Builder
    public static class AdjustmentResponse {
        private UUID id;
        private UUID organizationId;
        private UUID warehouseId;
        private String adjustmentNumber;
        private String reason;
        private AdjustmentStatus status;
        private String notes;
        private Instant appliedAt;
        private List<AdjustmentLineResponse> lines;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
