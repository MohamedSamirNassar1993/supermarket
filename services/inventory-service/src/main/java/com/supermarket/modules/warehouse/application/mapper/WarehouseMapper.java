package com.supermarket.modules.warehouse.application.mapper;

import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.TransferLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseTransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseDtos.WarehouseResponse toResponse(WarehouseEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "warehouseType", expression = "java(request.getWarehouseType() != null ? request.getWarehouseType() : \"STORAGE\")")
    WarehouseEntity toEntity(WarehouseDtos.WarehouseCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateWarehouse(WarehouseDtos.WarehouseUpdateRequest request, @MappingTarget WarehouseEntity entity);

    WarehouseDtos.TransferResponse toResponse(WarehouseTransferEntity entity);

    WarehouseDtos.TransferLineResponse toResponse(TransferLineEntity entity);

    WarehouseDtos.StockCountResponse toResponse(StockCountEntity entity);

    WarehouseDtos.StockCountLineResponse toResponse(StockCountLineEntity entity);

    WarehouseDtos.AdjustmentResponse toResponse(InventoryAdjustmentEntity entity);

    WarehouseDtos.AdjustmentLineResponse toResponse(InventoryAdjustmentLineEntity entity);
}
