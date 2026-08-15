package com.supermarket.modules.warehouse.application.service;

import com.supermarket.modules.platform.application.OrganizationGuard;
import com.supermarket.modules.platform.infrastructure.persistence.BranchJpaRepository;
import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.application.mapper.WarehouseMapper;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseRepository;
import com.supermarket.shared.exception.BusinessRuleException;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final BranchJpaRepository branchRepository;
    private final WarehouseMapper warehouseMapper;
    private final OrganizationGuard organizationGuard;

    @Transactional(readOnly = true)
    public Page<WarehouseDtos.WarehouseResponse> list(UUID organizationId, Pageable pageable) {
        organizationGuard.requireOrganization(organizationId);
        return warehouseRepository.findByOrganizationId(organizationId, pageable)
                .map(warehouseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public WarehouseDtos.WarehouseResponse get(UUID organizationId, UUID id) {
        return warehouseMapper.toResponse(findEntity(organizationId, id));
    }

    @Transactional
    public WarehouseDtos.WarehouseResponse create(WarehouseDtos.WarehouseCreateRequest request) {
        organizationGuard.requireOrganization(request.getOrganizationId());
        branchRepository.findByIdAndOrganizationId(request.getBranchId(), request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", request.getBranchId()));
        if (warehouseRepository.existsByOrganizationIdAndCode(request.getOrganizationId(), request.getCode())) {
            throw new BusinessRuleException("Warehouse code already exists: " + request.getCode());
        }
        WarehouseEntity entity = warehouseMapper.toEntity(request);
        return warehouseMapper.toResponse(warehouseRepository.save(entity));
    }

    @Transactional
    public WarehouseDtos.WarehouseResponse update(UUID organizationId, UUID id, WarehouseDtos.WarehouseUpdateRequest request) {
        WarehouseEntity entity = findEntity(organizationId, id);
        warehouseMapper.updateWarehouse(request, entity);
        return warehouseMapper.toResponse(warehouseRepository.save(entity));
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        WarehouseEntity entity = findEntity(organizationId, id);
        entity.setActive(false);
        warehouseRepository.save(entity);
    }

    public WarehouseEntity findEntity(UUID organizationId, UUID id) {
        organizationGuard.requireOrganization(organizationId);
        return warehouseRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }
}
