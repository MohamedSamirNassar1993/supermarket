package com.supermarket.modules.inventory.application.mapper;

import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.infrastructure.persistence.BatchAllocationEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.InventoryMovementEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.OrganizationSettingsEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDtos.SettingsResponse toResponse(OrganizationSettingsEntity entity);

    InventoryDtos.StockBatchResponse toResponse(StockBatchEntity entity);

    InventoryDtos.MovementResponse toResponse(InventoryMovementEntity entity);

    InventoryDtos.AllocationResponse toResponse(BatchAllocationEntity entity);
}
