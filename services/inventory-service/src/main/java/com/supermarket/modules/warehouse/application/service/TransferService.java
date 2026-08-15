package com.supermarket.modules.warehouse.application.service;

import com.supermarket.modules.inventory.application.service.InventoryMovementService;
import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.application.mapper.WarehouseMapper;
import com.supermarket.modules.warehouse.domain.TransferStatus;
import com.supermarket.modules.warehouse.infrastructure.persistence.TransferLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseTransferEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseTransferRepository;
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
public class TransferService {

    private final WarehouseTransferRepository transferRepository;
    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;
    private final InventoryMovementService inventoryMovementService;

    @Transactional(readOnly = true)
    public Page<WarehouseDtos.TransferResponse> list(UUID organizationId, TransferStatus status, Pageable pageable) {
        Page<WarehouseTransferEntity> page = status == null
                ? transferRepository.findByOrganizationId(organizationId, pageable)
                : transferRepository.findByOrganizationIdAndStatus(organizationId, status, pageable);
        return page.map(this::toDetailedResponse);
    }

    @Transactional(readOnly = true)
    public WarehouseDtos.TransferResponse get(UUID organizationId, UUID id) {
        return toDetailedResponse(findEntity(organizationId, id));
    }

    @Transactional
    public WarehouseDtos.TransferResponse create(WarehouseDtos.TransferCreateRequest request) {
        warehouseService.findEntity(request.getOrganizationId(), request.getFromWarehouseId());
        warehouseService.findEntity(request.getOrganizationId(), request.getToWarehouseId());
        if (request.getFromWarehouseId().equals(request.getToWarehouseId())) {
            throw new BusinessRuleException("Source and destination warehouses must differ");
        }
        if (transferRepository.existsByOrganizationIdAndTransferNumber(request.getOrganizationId(), request.getTransferNumber())) {
            throw new BusinessRuleException("Transfer number already exists: " + request.getTransferNumber());
        }

        WarehouseTransferEntity transfer = new WarehouseTransferEntity();
        transfer.setOrganizationId(request.getOrganizationId());
        transfer.setFromWarehouseId(request.getFromWarehouseId());
        transfer.setToWarehouseId(request.getToWarehouseId());
        transfer.setTransferNumber(request.getTransferNumber());
        transfer.setNotes(request.getNotes());
        transfer.setStatus(TransferStatus.DRAFT);

        for (WarehouseDtos.TransferLineRequest lineReq : request.getLines()) {
            TransferLineEntity line = new TransferLineEntity();
            line.setTransfer(transfer);
            line.setProductId(lineReq.getProductId());
            line.setVariantId(lineReq.getVariantId());
            line.setQuantity(lineReq.getQuantity());
            line.setReceivedQuantity(BigDecimal.ZERO);
            transfer.getLines().add(line);
        }

        return toDetailedResponse(transferRepository.save(transfer));
    }

    @Transactional
    public WarehouseDtos.TransferResponse ship(UUID organizationId, UUID id) {
        WarehouseTransferEntity transfer = findEntity(organizationId, id);
        if (transfer.getStatus() != TransferStatus.DRAFT) {
            throw new BusinessRuleException("Only draft transfers can be shipped");
        }
        for (TransferLineEntity line : transfer.getLines()) {
            inventoryMovementService.recordTransferOut(
                    organizationId,
                    transfer.getFromWarehouseId(),
                    line.getProductId(),
                    line.getVariantId(),
                    line.getQuantity(),
                    "WAREHOUSE_TRANSFER",
                    transfer.getId()
            );
        }
        transfer.setStatus(TransferStatus.IN_TRANSIT);
        transfer.setShippedAt(Instant.now());
        return toDetailedResponse(transferRepository.save(transfer));
    }

    @Transactional
    public WarehouseDtos.TransferResponse receive(UUID organizationId, UUID id) {
        WarehouseTransferEntity transfer = findEntity(organizationId, id);
        if (transfer.getStatus() != TransferStatus.IN_TRANSIT) {
            throw new BusinessRuleException("Only in-transit transfers can be received");
        }
        for (TransferLineEntity line : transfer.getLines()) {
            inventoryMovementService.recordTransferIn(
                    organizationId,
                    transfer.getToWarehouseId(),
                    line.getProductId(),
                    line.getVariantId(),
                    line.getQuantity(),
                    "WAREHOUSE_TRANSFER",
                    transfer.getId()
            );
            line.setReceivedQuantity(line.getQuantity());
        }
        transfer.setStatus(TransferStatus.RECEIVED);
        transfer.setReceivedAt(Instant.now());
        return toDetailedResponse(transferRepository.save(transfer));
    }

    @Transactional
    public WarehouseDtos.TransferResponse cancel(UUID organizationId, UUID id) {
        WarehouseTransferEntity transfer = findEntity(organizationId, id);
        if (transfer.getStatus() == TransferStatus.RECEIVED) {
            throw new BusinessRuleException("Received transfers cannot be cancelled");
        }
        if (transfer.getStatus() == TransferStatus.IN_TRANSIT) {
            throw new BusinessRuleException("In-transit transfers must be received, not cancelled");
        }
        transfer.setStatus(TransferStatus.CANCELLED);
        return toDetailedResponse(transferRepository.save(transfer));
    }

    private WarehouseTransferEntity findEntity(UUID organizationId, UUID id) {
        return transferRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse transfer", id));
    }

    private WarehouseDtos.TransferResponse toDetailedResponse(WarehouseTransferEntity entity) {
        WarehouseDtos.TransferResponse response = warehouseMapper.toResponse(entity);
        List<WarehouseDtos.TransferLineResponse> lines = entity.getLines().stream()
                .map(warehouseMapper::toResponse).toList();
        return WarehouseDtos.TransferResponse.builder()
                .id(response.getId())
                .organizationId(response.getOrganizationId())
                .fromWarehouseId(response.getFromWarehouseId())
                .toWarehouseId(response.getToWarehouseId())
                .transferNumber(response.getTransferNumber())
                .status(response.getStatus())
                .notes(response.getNotes())
                .shippedAt(response.getShippedAt())
                .receivedAt(response.getReceivedAt())
                .lines(lines)
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
