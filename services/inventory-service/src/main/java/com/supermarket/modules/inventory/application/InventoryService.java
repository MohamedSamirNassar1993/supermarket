package com.supermarket.modules.inventory.application;

import com.supermarket.modules.inventory.application.dto.AllocateStockRequest;
import com.supermarket.modules.inventory.application.dto.BatchAllocation;
import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.application.dto.ReceiveStockRequest;
import com.supermarket.modules.inventory.application.service.InventoryMovementService;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchRepository;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseRepository;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.MoneyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Legacy facade for purchase and sales modules. Delegates to {@link InventoryMovementService}.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    public static final String MOVEMENT_RECEIVE = "RECEIVE";
    public static final String MOVEMENT_SALE = "SALE";
    public static final String MOVEMENT_RETURN = "RETURN";
    public static final String MOVEMENT_PURCHASE_RETURN = "PURCHASE_RETURN";

    private final InventoryMovementService movementService;
    private final StockBatchRepository stockBatchRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public StockBatchEntity receiveStock(ReceiveStockRequest request) {
        UUID warehouseId = resolveWarehouseId(request.getBranchId());

        InventoryDtos.StockInRequest inRequest = new InventoryDtos.StockInRequest();
        inRequest.setOrganizationId(request.getOrganizationId());
        inRequest.setWarehouseId(warehouseId);
        inRequest.setProductId(request.getProductId());
        inRequest.setQuantity(MoneyUtils.scale(request.getQuantity()));
        inRequest.setUnitCost(MoneyUtils.scale(request.getUnitCost()));
        inRequest.setBatchNumber(request.getBatchNumber());
        inRequest.setExpiryDate(request.getExpiryDate());
        inRequest.setNotes("Stock received via " + request.getSourceType());

        InventoryDtos.MovementResponse movement = movementService.recordStockIn(inRequest);
        return stockBatchRepository.findById(movement.getBatchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to locate received batch"));
    }

    @Transactional
    public List<BatchAllocation> allocateForSale(AllocateStockRequest request) {
        UUID warehouseId = resolveWarehouseId(request.getBranchId());

        InventoryDtos.StockOutRequest outRequest = new InventoryDtos.StockOutRequest();
        outRequest.setOrganizationId(request.getOrganizationId());
        outRequest.setWarehouseId(warehouseId);
        outRequest.setProductId(request.getProductId());
        outRequest.setQuantity(MoneyUtils.scale(request.getQuantity()));
        outRequest.setNotes("Allocated for " + request.getReferenceType());

        var movement = movementService.recordStockOut(outRequest);
        return movement.getAllocations().stream()
                .map(a -> BatchAllocation.builder().batchId(a.getBatchId()).quantity(a.getQuantity()).build())
                .toList();
    }

    @Transactional
    public StockBatchEntity returnToStock(UUID organizationId, UUID branchId, UUID productId, UUID batchId,
                                          BigDecimal quantity, String referenceType, UUID referenceId) {
        if (batchId != null) {
            StockBatchEntity batch = stockBatchRepository.findById(batchId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Stock batch not found"));
            InventoryDtos.StockInRequest inRequest = new InventoryDtos.StockInRequest();
            inRequest.setOrganizationId(organizationId);
            inRequest.setWarehouseId(batch.getWarehouseId());
            inRequest.setProductId(productId);
            inRequest.setQuantity(MoneyUtils.scale(quantity));
            inRequest.setUnitCost(batch.getUnitCost());
            inRequest.setBatchNumber(batch.getBatchNumber() + "-RET");
            inRequest.setNotes("Sales return");
            movementService.recordStockIn(inRequest);
            return batch;
        }
        return receiveStock(ReceiveStockRequest.builder()
                .organizationId(organizationId)
                .branchId(branchId)
                .productId(productId)
                .quantity(quantity)
                .unitCost(BigDecimal.ZERO)
                .sourceType(referenceType)
                .sourceId(referenceId)
                .build());
    }

    @Transactional
    public void deductForPurchaseReturn(UUID batchId, BigDecimal quantity, String referenceType, UUID referenceId) {
        StockBatchEntity batch = stockBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Stock batch not found"));
        if (batch.getRemainingQuantity().compareTo(quantity) < 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Insufficient batch quantity for purchase return");
        }

        InventoryDtos.StockOutRequest outRequest = new InventoryDtos.StockOutRequest();
        outRequest.setOrganizationId(batch.getOrganizationId());
        outRequest.setWarehouseId(batch.getWarehouseId());
        outRequest.setProductId(batch.getProductId());
        outRequest.setVariantId(batch.getVariantId());
        outRequest.setQuantity(MoneyUtils.scale(quantity));
        outRequest.setNotes("Purchase return " + referenceId);
        movementService.recordStockOut(outRequest);
    }

    private UUID resolveWarehouseId(UUID branchId) {
        return warehouseRepository.findFirstByBranchIdAndActiveTrue(branchId)
                .map(WarehouseEntity::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
                        "No active warehouse found for branch: " + branchId));
    }
}
