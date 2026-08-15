package com.supermarket.modules.inventory.application.service;

import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.application.mapper.InventoryMapper;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchRepository;
import com.supermarket.modules.platform.application.OrganizationGuard;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockBatchService {

    private final StockBatchRepository stockBatchRepository;
    private final OrganizationGuard organizationGuard;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public Page<InventoryDtos.StockBatchResponse> listByOrganization(UUID organizationId, Pageable pageable) {
        organizationGuard.requireOrganization(organizationId);
        return stockBatchRepository.findByOrganizationId(organizationId, pageable)
                .map(inventoryMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDtos.StockBatchResponse> listByWarehouse(UUID warehouseId, Pageable pageable) {
        return stockBatchRepository.findByWarehouseId(warehouseId, pageable)
                .map(inventoryMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public InventoryDtos.StockBatchResponse get(UUID organizationId, UUID id) {
        return inventoryMapper.toResponse(findEntity(organizationId, id));
    }

    @Transactional
    public InventoryDtos.StockBatchResponse create(InventoryDtos.StockBatchCreateRequest request) {
        organizationGuard.requireOrganization(request.getOrganizationId());
        StockBatchEntity batch = new StockBatchEntity();
        batch.setOrganizationId(request.getOrganizationId());
        batch.setWarehouseId(request.getWarehouseId());
        batch.setProductId(request.getProductId());
        batch.setVariantId(request.getVariantId());
        batch.setBatchNumber(request.getBatchNumber());
        batch.setQuantity(request.getQuantity());
        batch.setRemainingQuantity(request.getQuantity());
        batch.setUnitCost(request.getUnitCost());
        batch.setReceivedAt(Instant.now());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setSourceType(request.getSourceType());
        batch.setSourceId(request.getSourceId());
        return inventoryMapper.toResponse(stockBatchRepository.save(batch));
    }

    StockBatchEntity findEntity(UUID organizationId, UUID id) {
        organizationGuard.requireOrganization(organizationId);
        return stockBatchRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock batch", id));
    }
}
