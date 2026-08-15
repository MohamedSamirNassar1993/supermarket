package com.supermarket.modules.warehouse.application.service;

import com.supermarket.modules.inventory.application.service.InventoryMovementService;
import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.application.mapper.WarehouseMapper;
import com.supermarket.modules.warehouse.domain.AdjustmentStatus;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentRepository;
import com.supermarket.shared.exception.BusinessRuleException;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdjustmentService {

    private final InventoryAdjustmentRepository adjustmentRepository;
    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;
    private final InventoryMovementService inventoryMovementService;

    @Transactional(readOnly = true)
    public Page<WarehouseDtos.AdjustmentResponse> list(UUID organizationId, Pageable pageable) {
        return adjustmentRepository.findByOrganizationId(organizationId, pageable)
                .map(this::toDetailedResponse);
    }

    @Transactional(readOnly = true)
    public WarehouseDtos.AdjustmentResponse get(UUID organizationId, UUID id) {
        return toDetailedResponse(findEntity(organizationId, id));
    }

    @Transactional
    public WarehouseDtos.AdjustmentResponse create(WarehouseDtos.AdjustmentCreateRequest request) {
        warehouseService.findEntity(request.getOrganizationId(), request.getWarehouseId());
        if (adjustmentRepository.existsByOrganizationIdAndAdjustmentNumber(request.getOrganizationId(), request.getAdjustmentNumber())) {
            throw new BusinessRuleException("Adjustment number already exists: " + request.getAdjustmentNumber());
        }

        InventoryAdjustmentEntity adjustment = new InventoryAdjustmentEntity();
        adjustment.setOrganizationId(request.getOrganizationId());
        adjustment.setWarehouseId(request.getWarehouseId());
        adjustment.setAdjustmentNumber(request.getAdjustmentNumber());
        adjustment.setReason(request.getReason());
        adjustment.setNotes(request.getNotes());
        adjustment.setStatus(AdjustmentStatus.DRAFT);

        for (WarehouseDtos.AdjustmentLineRequest lineReq : request.getLines()) {
            InventoryAdjustmentLineEntity line = new InventoryAdjustmentLineEntity();
            line.setAdjustment(adjustment);
            line.setProductId(lineReq.getProductId());
            line.setVariantId(lineReq.getVariantId());
            line.setQuantityChange(lineReq.getQuantityChange());
            line.setUnitCost(lineReq.getUnitCost());
            line.setNotes(lineReq.getNotes());
            adjustment.getLines().add(line);
        }

        return toDetailedResponse(adjustmentRepository.save(adjustment));
    }

    @Transactional
    public WarehouseDtos.AdjustmentResponse apply(UUID organizationId, UUID id) {
        InventoryAdjustmentEntity adjustment = findEntity(organizationId, id);
        if (adjustment.getStatus() != AdjustmentStatus.DRAFT) {
            throw new BusinessRuleException("Only draft adjustments can be applied");
        }
        for (InventoryAdjustmentLineEntity line : adjustment.getLines()) {
            inventoryMovementService.recordAdjustment(
                    organizationId,
                    adjustment.getWarehouseId(),
                    line.getProductId(),
                    line.getVariantId(),
                    line.getQuantityChange(),
                    line.getUnitCost(),
                    "INVENTORY_ADJUSTMENT",
                    adjustment.getId()
            );
        }
        adjustment.setStatus(AdjustmentStatus.APPLIED);
        adjustment.setAppliedAt(Instant.now());
        return toDetailedResponse(adjustmentRepository.save(adjustment));
    }

    @Transactional
    public WarehouseDtos.AdjustmentResponse cancel(UUID organizationId, UUID id) {
        InventoryAdjustmentEntity adjustment = findEntity(organizationId, id);
        if (adjustment.getStatus() == AdjustmentStatus.APPLIED) {
            throw new BusinessRuleException("Applied adjustments cannot be cancelled");
        }
        adjustment.setStatus(AdjustmentStatus.CANCELLED);
        return toDetailedResponse(adjustmentRepository.save(adjustment));
    }

    private InventoryAdjustmentEntity findEntity(UUID organizationId, UUID id) {
        return adjustmentRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory adjustment", id));
    }

    private WarehouseDtos.AdjustmentResponse toDetailedResponse(InventoryAdjustmentEntity entity) {
        WarehouseDtos.AdjustmentResponse response = warehouseMapper.toResponse(entity);
        List<WarehouseDtos.AdjustmentLineResponse> lines = entity.getLines().stream()
                .map(warehouseMapper::toResponse).toList();
        return WarehouseDtos.AdjustmentResponse.builder()
                .id(response.getId())
                .organizationId(response.getOrganizationId())
                .warehouseId(response.getWarehouseId())
                .adjustmentNumber(response.getAdjustmentNumber())
                .reason(response.getReason())
                .status(response.getStatus())
                .notes(response.getNotes())
                .appliedAt(response.getAppliedAt())
                .lines(lines)
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
