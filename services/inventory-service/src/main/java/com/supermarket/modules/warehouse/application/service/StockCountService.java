package com.supermarket.modules.warehouse.application.service;

import com.supermarket.modules.inventory.application.service.InventoryMovementService;
import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.application.mapper.WarehouseMapper;
import com.supermarket.modules.warehouse.domain.StockCountStatus;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountRepository;
import com.supermarket.shared.exception.BusinessRuleException;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockCountService {

    private final StockCountRepository stockCountRepository;
    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;
    private final InventoryMovementService inventoryMovementService;

    @Transactional(readOnly = true)
    public Page<WarehouseDtos.StockCountResponse> list(UUID organizationId, Pageable pageable) {
        return stockCountRepository.findByOrganizationId(organizationId, pageable)
                .map(this::toDetailedResponse);
    }

    @Transactional(readOnly = true)
    public WarehouseDtos.StockCountResponse get(UUID organizationId, UUID id) {
        return toDetailedResponse(findEntity(organizationId, id));
    }

    @Transactional
    public WarehouseDtos.StockCountResponse create(WarehouseDtos.StockCountCreateRequest request) {
        warehouseService.findEntity(request.getOrganizationId(), request.getWarehouseId());
        if (stockCountRepository.existsByOrganizationIdAndCountNumber(request.getOrganizationId(), request.getCountNumber())) {
            throw new BusinessRuleException("Stock count number already exists: " + request.getCountNumber());
        }

        StockCountEntity count = new StockCountEntity();
        count.setOrganizationId(request.getOrganizationId());
        count.setWarehouseId(request.getWarehouseId());
        count.setCountNumber(request.getCountNumber());
        count.setNotes(request.getNotes());
        count.setStatus(StockCountStatus.DRAFT);

        for (WarehouseDtos.StockCountLineRequest lineReq : request.getLines()) {
            StockCountLineEntity line = new StockCountLineEntity();
            line.setStockCount(count);
            line.setProductId(lineReq.getProductId());
            line.setVariantId(lineReq.getVariantId());
            line.setSystemQuantity(lineReq.getSystemQuantity());
            line.setCountedQuantity(lineReq.getCountedQuantity());
            if (lineReq.getCountedQuantity() != null) {
                line.setVariance(lineReq.getCountedQuantity().subtract(lineReq.getSystemQuantity()));
            }
            count.getLines().add(line);
        }

        return toDetailedResponse(stockCountRepository.save(count));
    }

    @Transactional
    public WarehouseDtos.StockCountResponse start(UUID organizationId, UUID id) {
        StockCountEntity count = findEntity(organizationId, id);
        if (count.getStatus() != StockCountStatus.DRAFT) {
            throw new BusinessRuleException("Only draft stock counts can be started");
        }
        count.setStatus(StockCountStatus.IN_PROGRESS);
        count.setCountedAt(Instant.now());
        return toDetailedResponse(stockCountRepository.save(count));
    }

    @Transactional
    public WarehouseDtos.StockCountResponse complete(UUID organizationId, UUID id) {
        StockCountEntity count = findEntity(organizationId, id);
        if (count.getStatus() != StockCountStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Only in-progress stock counts can be completed");
        }
        for (StockCountLineEntity line : count.getLines()) {
            if (line.getCountedQuantity() == null) {
                throw new BusinessRuleException("All lines must have counted quantities before completion");
            }
            line.setVariance(line.getCountedQuantity().subtract(line.getSystemQuantity()));
            if (line.getVariance().compareTo(BigDecimal.ZERO) != 0) {
                inventoryMovementService.recordAdjustment(
                        organizationId,
                        count.getWarehouseId(),
                        line.getProductId(),
                        line.getVariantId(),
                        line.getVariance(),
                        null,
                        "STOCK_COUNT",
                        count.getId()
                );
            }
        }
        count.setStatus(StockCountStatus.COMPLETED);
        count.setCompletedAt(Instant.now());
        return toDetailedResponse(stockCountRepository.save(count));
    }

    private StockCountEntity findEntity(UUID organizationId, UUID id) {
        return stockCountRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock count", id));
    }

    private WarehouseDtos.StockCountResponse toDetailedResponse(StockCountEntity entity) {
        WarehouseDtos.StockCountResponse response = warehouseMapper.toResponse(entity);
        List<WarehouseDtos.StockCountLineResponse> lines = entity.getLines().stream()
                .map(warehouseMapper::toResponse).toList();
        return WarehouseDtos.StockCountResponse.builder()
                .id(response.getId())
                .organizationId(response.getOrganizationId())
                .warehouseId(response.getWarehouseId())
                .countNumber(response.getCountNumber())
                .status(response.getStatus())
                .notes(response.getNotes())
                .countedAt(response.getCountedAt())
                .completedAt(response.getCompletedAt())
                .lines(lines)
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
