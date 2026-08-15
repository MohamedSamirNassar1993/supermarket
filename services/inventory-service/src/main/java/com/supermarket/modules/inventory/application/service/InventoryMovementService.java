package com.supermarket.modules.inventory.application.service;

import com.supermarket.modules.inventory.application.cogs.CogsAllocation;
import com.supermarket.modules.inventory.application.cogs.CogsCalculationService;
import com.supermarket.modules.inventory.application.cogs.CogsResult;
import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.application.mapper.InventoryMapper;
import com.supermarket.modules.inventory.domain.MovementType;
import com.supermarket.modules.inventory.domain.ValuationMethod;
import com.supermarket.modules.inventory.infrastructure.persistence.BatchAllocationEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.InventoryMovementEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.InventoryMovementRepository;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchRepository;
import com.supermarket.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryMovementService {

    private final InventoryMovementRepository movementRepository;
    private final StockBatchRepository stockBatchRepository;
    private final OrganizationSettingsService settingsService;
    private final CogsCalculationService cogsCalculationService;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryDtos.MovementResponse recordStockIn(InventoryDtos.StockInRequest request) {
        String batchNumber = request.getBatchNumber() != null
                ? request.getBatchNumber()
                : "BATCH-" + Instant.now().toEpochMilli();

        StockBatchEntity batch = new StockBatchEntity();
        batch.setOrganizationId(request.getOrganizationId());
        batch.setWarehouseId(request.getWarehouseId());
        batch.setProductId(request.getProductId());
        batch.setVariantId(request.getVariantId());
        batch.setBatchNumber(batchNumber);
        batch.setQuantity(request.getQuantity());
        batch.setRemainingQuantity(request.getQuantity());
        batch.setUnitCost(request.getUnitCost());
        batch.setReceivedAt(Instant.now());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setSourceType("MANUAL_IN");
        batch = stockBatchRepository.save(batch);

        InventoryMovementEntity movement = buildMovement(
                request.getOrganizationId(),
                request.getWarehouseId(),
                request.getProductId(),
                request.getVariantId(),
                MovementType.IN,
                request.getQuantity(),
                request.getUnitCost(),
                request.getQuantity().multiply(request.getUnitCost()),
                "MANUAL_IN",
                null,
                request.getNotes()
        );

        BatchAllocationEntity allocation = new BatchAllocationEntity();
        allocation.setMovement(movement);
        allocation.setBatchId(batch.getId());
        allocation.setBatch(batch);
        allocation.setQuantity(request.getQuantity());
        allocation.setUnitCost(request.getUnitCost());
        allocation.setTotalCost(request.getQuantity().multiply(request.getUnitCost()));
        movement.getAllocations().add(allocation);

        return toDetailedResponse(movementRepository.save(movement));
    }

    @Transactional
    public InventoryDtos.MovementResponse recordStockOut(InventoryDtos.StockOutRequest request) {
        return recordOutbound(
                request.getOrganizationId(),
                request.getWarehouseId(),
                request.getProductId(),
                request.getVariantId(),
                request.getQuantity(),
                MovementType.OUT,
                "MANUAL_OUT",
                null,
                request.getNotes()
        );
    }

    @Transactional
    public void recordTransferOut(UUID organizationId, UUID warehouseId, UUID productId, UUID variantId,
                                  BigDecimal quantity, String referenceType, UUID referenceId) {
        recordOutbound(organizationId, warehouseId, productId, variantId, quantity,
                MovementType.TRANSFER_OUT, referenceType, referenceId, null);
    }

    @Transactional
    public void recordTransferIn(UUID organizationId, UUID warehouseId, UUID productId, UUID variantId,
                                 BigDecimal quantity, String referenceType, UUID referenceId) {
        BigDecimal unitCost = resolveAverageCost(warehouseId, productId, variantId);
        if (unitCost.compareTo(BigDecimal.ZERO) == 0) {
            unitCost = BigDecimal.ZERO;
        }

        StockBatchEntity batch = new StockBatchEntity();
        batch.setOrganizationId(organizationId);
        batch.setWarehouseId(warehouseId);
        batch.setProductId(productId);
        batch.setVariantId(variantId);
        batch.setBatchNumber("TRF-" + referenceId + "-" + Instant.now().toEpochMilli());
        batch.setQuantity(quantity);
        batch.setRemainingQuantity(quantity);
        batch.setUnitCost(unitCost);
        batch.setReceivedAt(Instant.now());
        batch.setSourceType(referenceType);
        batch.setSourceId(referenceId);
        stockBatchRepository.save(batch);

        InventoryMovementEntity movement = buildMovement(
                organizationId, warehouseId, productId, variantId,
                MovementType.TRANSFER_IN, quantity, unitCost, quantity.multiply(unitCost),
                referenceType, referenceId, null
        );
        movementRepository.save(movement);
    }

    @Transactional
    public void recordAdjustment(UUID organizationId, UUID warehouseId, UUID productId, UUID variantId,
                                 BigDecimal quantityChange, BigDecimal unitCost, String referenceType, UUID referenceId) {
        if (quantityChange.compareTo(BigDecimal.ZERO) > 0) {
            InventoryDtos.StockInRequest inRequest = new InventoryDtos.StockInRequest();
            inRequest.setOrganizationId(organizationId);
            inRequest.setWarehouseId(warehouseId);
            inRequest.setProductId(productId);
            inRequest.setVariantId(variantId);
            inRequest.setQuantity(quantityChange);
            inRequest.setUnitCost(unitCost != null ? unitCost : BigDecimal.ZERO);
            inRequest.setBatchNumber("ADJ-" + referenceId);
            inRequest.setNotes("Adjustment increase");
            recordStockIn(inRequest);
        } else {
            recordOutbound(organizationId, warehouseId, productId, variantId,
                    quantityChange.abs(), MovementType.ADJUSTMENT, referenceType, referenceId, null);
        }
    }

    private InventoryDtos.MovementResponse recordOutbound(UUID organizationId, UUID warehouseId, UUID productId,
                                                          UUID variantId, BigDecimal quantity, MovementType type,
                                                          String referenceType, UUID referenceId, String notes) {
        validateStockAvailable(organizationId, warehouseId, productId, variantId, quantity);

        ValuationMethod method = settingsService.getValuationMethod(organizationId);
        List<StockBatchEntity> batches = stockBatchRepository.findAvailableBatches(warehouseId, productId, variantId);
        CogsResult cogsResult = cogsCalculationService.calculate(method, batches, quantity);

        applyAllocations(method, batches, cogsResult);

        InventoryMovementEntity movement = buildMovement(
                organizationId, warehouseId, productId, variantId,
                type, quantity.negate(), cogsResult.getAverageUnitCost(), cogsResult.getTotalCost(),
                referenceType, referenceId, notes
        );

        for (CogsAllocation alloc : cogsResult.getAllocations()) {
            if (alloc.getBatchId() == null) {
                continue;
            }
            BatchAllocationEntity entity = new BatchAllocationEntity();
            entity.setMovement(movement);
            entity.setBatchId(alloc.getBatchId());
            entity.setQuantity(alloc.getQuantity());
            entity.setUnitCost(alloc.getUnitCost());
            entity.setTotalCost(alloc.getTotalCost());
            movement.getAllocations().add(entity);
        }

        return toDetailedResponse(movementRepository.save(movement));
    }

    private void applyAllocations(ValuationMethod method, List<StockBatchEntity> batches, CogsResult cogsResult) {
        if (method == ValuationMethod.WEIGHTED_AVERAGE) {
            BigDecimal remaining = cogsResult.getTotalQuantity();
            for (StockBatchEntity batch : batches) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                BigDecimal allocated = remaining.min(batch.getRemainingQuantity());
                batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(allocated));
                stockBatchRepository.save(batch);
                remaining = remaining.subtract(allocated);
            }
        } else {
            for (CogsAllocation alloc : cogsResult.getAllocations()) {
                StockBatchEntity batch = stockBatchRepository.findById(alloc.getBatchId()).orElseThrow();
                batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(alloc.getQuantity()));
                stockBatchRepository.save(batch);
            }
        }
    }

    private void validateStockAvailable(UUID organizationId, UUID warehouseId, UUID productId,
                                        UUID variantId, BigDecimal quantity) {
        if (settingsService.isNegativeStockAllowed(organizationId)) {
            return;
        }
        BigDecimal available = stockBatchRepository.sumRemainingQuantity(warehouseId, productId, variantId);
        if (available == null || available.compareTo(quantity) < 0) {
            throw new BusinessRuleException(
                    String.format("Insufficient stock. Available: %s, requested: %s", available, quantity));
        }
    }

    private BigDecimal resolveAverageCost(UUID warehouseId, UUID productId, UUID variantId) {
        List<StockBatchEntity> batches = stockBatchRepository.findAvailableBatches(warehouseId, productId, variantId);
        BigDecimal totalQty = batches.stream().map(StockBatchEntity::getRemainingQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalQty.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalValue = batches.stream()
                .map(b -> b.getRemainingQuantity().multiply(b.getUnitCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalValue.divide(totalQty, 4, java.math.RoundingMode.HALF_UP);
    }

    private InventoryMovementEntity buildMovement(UUID organizationId, UUID warehouseId, UUID productId, UUID variantId,
                                                MovementType type, BigDecimal quantity, BigDecimal unitCost,
                                                BigDecimal totalCost, String referenceType, UUID referenceId, String notes) {
        InventoryMovementEntity movement = new InventoryMovementEntity();
        movement.setOrganizationId(organizationId);
        movement.setWarehouseId(warehouseId);
        movement.setProductId(productId);
        movement.setVariantId(variantId);
        movement.setMovementType(type);
        movement.setQuantity(quantity);
        movement.setUnitCost(unitCost);
        movement.setTotalCost(totalCost);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);
        movement.setNotes(notes);
        movement.setOccurredAt(Instant.now());
        return movement;
    }

    private InventoryDtos.MovementResponse toDetailedResponse(InventoryMovementEntity entity) {
        InventoryDtos.MovementResponse response = inventoryMapper.toResponse(entity);
        List<InventoryDtos.AllocationResponse> allocations = entity.getAllocations().stream()
                .map(inventoryMapper::toResponse).toList();
        UUID batchId = entity.getAllocations().isEmpty() ? null : entity.getAllocations().get(0).getBatchId();
        return InventoryDtos.MovementResponse.builder()
                .id(response.getId())
                .batchId(batchId)
                .organizationId(response.getOrganizationId())
                .warehouseId(response.getWarehouseId())
                .productId(response.getProductId())
                .variantId(response.getVariantId())
                .movementType(response.getMovementType())
                .quantity(response.getQuantity())
                .unitCost(response.getUnitCost())
                .totalCost(response.getTotalCost())
                .referenceType(response.getReferenceType())
                .referenceId(response.getReferenceId())
                .notes(response.getNotes())
                .occurredAt(response.getOccurredAt())
                .allocations(allocations)
                .build();
    }
}
