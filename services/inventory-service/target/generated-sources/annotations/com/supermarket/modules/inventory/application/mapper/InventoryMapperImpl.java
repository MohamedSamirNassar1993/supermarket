package com.supermarket.modules.inventory.application.mapper;

import com.supermarket.modules.inventory.application.dto.InventoryDtos;
import com.supermarket.modules.inventory.infrastructure.persistence.BatchAllocationEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.InventoryMovementEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.OrganizationSettingsEntity;
import com.supermarket.modules.inventory.infrastructure.persistence.StockBatchEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-11T02:21:08+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryDtos.SettingsResponse toResponse(OrganizationSettingsEntity entity) {
        if ( entity == null ) {
            return null;
        }

        InventoryDtos.SettingsResponse.SettingsResponseBuilder settingsResponse = InventoryDtos.SettingsResponse.builder();

        settingsResponse.id( entity.getId() );
        settingsResponse.organizationId( entity.getOrganizationId() );
        settingsResponse.valuationMethod( entity.getValuationMethod() );
        settingsResponse.allowNegativeStock( entity.isAllowNegativeStock() );
        settingsResponse.lowStockAlertEnabled( entity.isLowStockAlertEnabled() );
        settingsResponse.expiryAlertDays( entity.getExpiryAlertDays() );

        return settingsResponse.build();
    }

    @Override
    public InventoryDtos.StockBatchResponse toResponse(StockBatchEntity entity) {
        if ( entity == null ) {
            return null;
        }

        InventoryDtos.StockBatchResponse.StockBatchResponseBuilder stockBatchResponse = InventoryDtos.StockBatchResponse.builder();

        stockBatchResponse.id( entity.getId() );
        stockBatchResponse.organizationId( entity.getOrganizationId() );
        stockBatchResponse.warehouseId( entity.getWarehouseId() );
        stockBatchResponse.productId( entity.getProductId() );
        stockBatchResponse.variantId( entity.getVariantId() );
        stockBatchResponse.batchNumber( entity.getBatchNumber() );
        stockBatchResponse.quantity( entity.getQuantity() );
        stockBatchResponse.remainingQuantity( entity.getRemainingQuantity() );
        stockBatchResponse.unitCost( entity.getUnitCost() );
        stockBatchResponse.receivedAt( entity.getReceivedAt() );
        stockBatchResponse.expiryDate( entity.getExpiryDate() );
        stockBatchResponse.sourceType( entity.getSourceType() );
        stockBatchResponse.sourceId( entity.getSourceId() );
        stockBatchResponse.active( entity.isActive() );

        return stockBatchResponse.build();
    }

    @Override
    public InventoryDtos.MovementResponse toResponse(InventoryMovementEntity entity) {
        if ( entity == null ) {
            return null;
        }

        InventoryDtos.MovementResponse.MovementResponseBuilder movementResponse = InventoryDtos.MovementResponse.builder();

        movementResponse.id( entity.getId() );
        movementResponse.organizationId( entity.getOrganizationId() );
        movementResponse.warehouseId( entity.getWarehouseId() );
        movementResponse.productId( entity.getProductId() );
        movementResponse.variantId( entity.getVariantId() );
        movementResponse.movementType( entity.getMovementType() );
        movementResponse.quantity( entity.getQuantity() );
        movementResponse.unitCost( entity.getUnitCost() );
        movementResponse.totalCost( entity.getTotalCost() );
        movementResponse.referenceType( entity.getReferenceType() );
        movementResponse.referenceId( entity.getReferenceId() );
        movementResponse.notes( entity.getNotes() );
        movementResponse.occurredAt( entity.getOccurredAt() );
        movementResponse.allocations( batchAllocationEntityListToAllocationResponseList( entity.getAllocations() ) );

        return movementResponse.build();
    }

    @Override
    public InventoryDtos.AllocationResponse toResponse(BatchAllocationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        InventoryDtos.AllocationResponse.AllocationResponseBuilder allocationResponse = InventoryDtos.AllocationResponse.builder();

        allocationResponse.batchId( entity.getBatchId() );
        allocationResponse.quantity( entity.getQuantity() );
        allocationResponse.unitCost( entity.getUnitCost() );
        allocationResponse.totalCost( entity.getTotalCost() );

        return allocationResponse.build();
    }

    protected List<InventoryDtos.AllocationResponse> batchAllocationEntityListToAllocationResponseList(List<BatchAllocationEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<InventoryDtos.AllocationResponse> list1 = new ArrayList<InventoryDtos.AllocationResponse>( list.size() );
        for ( BatchAllocationEntity batchAllocationEntity : list ) {
            list1.add( toResponse( batchAllocationEntity ) );
        }

        return list1;
    }
}
