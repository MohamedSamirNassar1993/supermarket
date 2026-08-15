package com.supermarket.modules.warehouse.application.mapper;

import com.supermarket.modules.warehouse.application.dto.WarehouseDtos;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.InventoryAdjustmentLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.StockCountLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.TransferLineEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseEntity;
import com.supermarket.modules.warehouse.infrastructure.persistence.WarehouseTransferEntity;
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
public class WarehouseMapperImpl implements WarehouseMapper {

    @Override
    public WarehouseDtos.WarehouseResponse toResponse(WarehouseEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.WarehouseResponse.WarehouseResponseBuilder warehouseResponse = WarehouseDtos.WarehouseResponse.builder();

        warehouseResponse.id( entity.getId() );
        warehouseResponse.organizationId( entity.getOrganizationId() );
        warehouseResponse.branchId( entity.getBranchId() );
        warehouseResponse.code( entity.getCode() );
        warehouseResponse.name( entity.getName() );
        warehouseResponse.address( entity.getAddress() );
        warehouseResponse.warehouseType( entity.getWarehouseType() );
        warehouseResponse.active( entity.isActive() );
        warehouseResponse.createdAt( entity.getCreatedAt() );
        warehouseResponse.updatedAt( entity.getUpdatedAt() );

        return warehouseResponse.build();
    }

    @Override
    public WarehouseEntity toEntity(WarehouseDtos.WarehouseCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        WarehouseEntity warehouseEntity = new WarehouseEntity();

        warehouseEntity.setOrganizationId( request.getOrganizationId() );
        warehouseEntity.setBranchId( request.getBranchId() );
        warehouseEntity.setCode( request.getCode() );
        warehouseEntity.setName( request.getName() );
        warehouseEntity.setAddress( request.getAddress() );

        warehouseEntity.setActive( true );
        warehouseEntity.setWarehouseType( request.getWarehouseType() != null ? request.getWarehouseType() : "STORAGE" );

        return warehouseEntity;
    }

    @Override
    public void updateWarehouse(WarehouseDtos.WarehouseUpdateRequest request, WarehouseEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setName( request.getName() );
        entity.setAddress( request.getAddress() );
        entity.setWarehouseType( request.getWarehouseType() );
        if ( request.getActive() != null ) {
            entity.setActive( request.getActive() );
        }
    }

    @Override
    public WarehouseDtos.TransferResponse toResponse(WarehouseTransferEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.TransferResponse.TransferResponseBuilder transferResponse = WarehouseDtos.TransferResponse.builder();

        transferResponse.id( entity.getId() );
        transferResponse.organizationId( entity.getOrganizationId() );
        transferResponse.fromWarehouseId( entity.getFromWarehouseId() );
        transferResponse.toWarehouseId( entity.getToWarehouseId() );
        transferResponse.transferNumber( entity.getTransferNumber() );
        transferResponse.status( entity.getStatus() );
        transferResponse.notes( entity.getNotes() );
        transferResponse.shippedAt( entity.getShippedAt() );
        transferResponse.receivedAt( entity.getReceivedAt() );
        transferResponse.lines( transferLineEntityListToTransferLineResponseList( entity.getLines() ) );
        transferResponse.createdAt( entity.getCreatedAt() );
        transferResponse.updatedAt( entity.getUpdatedAt() );

        return transferResponse.build();
    }

    @Override
    public WarehouseDtos.TransferLineResponse toResponse(TransferLineEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.TransferLineResponse.TransferLineResponseBuilder transferLineResponse = WarehouseDtos.TransferLineResponse.builder();

        transferLineResponse.id( entity.getId() );
        transferLineResponse.productId( entity.getProductId() );
        transferLineResponse.variantId( entity.getVariantId() );
        transferLineResponse.quantity( entity.getQuantity() );
        transferLineResponse.receivedQuantity( entity.getReceivedQuantity() );

        return transferLineResponse.build();
    }

    @Override
    public WarehouseDtos.StockCountResponse toResponse(StockCountEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.StockCountResponse.StockCountResponseBuilder stockCountResponse = WarehouseDtos.StockCountResponse.builder();

        stockCountResponse.id( entity.getId() );
        stockCountResponse.organizationId( entity.getOrganizationId() );
        stockCountResponse.warehouseId( entity.getWarehouseId() );
        stockCountResponse.countNumber( entity.getCountNumber() );
        stockCountResponse.status( entity.getStatus() );
        stockCountResponse.notes( entity.getNotes() );
        stockCountResponse.countedAt( entity.getCountedAt() );
        stockCountResponse.completedAt( entity.getCompletedAt() );
        stockCountResponse.lines( stockCountLineEntityListToStockCountLineResponseList( entity.getLines() ) );
        stockCountResponse.createdAt( entity.getCreatedAt() );
        stockCountResponse.updatedAt( entity.getUpdatedAt() );

        return stockCountResponse.build();
    }

    @Override
    public WarehouseDtos.StockCountLineResponse toResponse(StockCountLineEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.StockCountLineResponse.StockCountLineResponseBuilder stockCountLineResponse = WarehouseDtos.StockCountLineResponse.builder();

        stockCountLineResponse.id( entity.getId() );
        stockCountLineResponse.productId( entity.getProductId() );
        stockCountLineResponse.variantId( entity.getVariantId() );
        stockCountLineResponse.systemQuantity( entity.getSystemQuantity() );
        stockCountLineResponse.countedQuantity( entity.getCountedQuantity() );
        stockCountLineResponse.variance( entity.getVariance() );

        return stockCountLineResponse.build();
    }

    @Override
    public WarehouseDtos.AdjustmentResponse toResponse(InventoryAdjustmentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.AdjustmentResponse.AdjustmentResponseBuilder adjustmentResponse = WarehouseDtos.AdjustmentResponse.builder();

        adjustmentResponse.id( entity.getId() );
        adjustmentResponse.organizationId( entity.getOrganizationId() );
        adjustmentResponse.warehouseId( entity.getWarehouseId() );
        adjustmentResponse.adjustmentNumber( entity.getAdjustmentNumber() );
        adjustmentResponse.reason( entity.getReason() );
        adjustmentResponse.status( entity.getStatus() );
        adjustmentResponse.notes( entity.getNotes() );
        adjustmentResponse.appliedAt( entity.getAppliedAt() );
        adjustmentResponse.lines( inventoryAdjustmentLineEntityListToAdjustmentLineResponseList( entity.getLines() ) );
        adjustmentResponse.createdAt( entity.getCreatedAt() );
        adjustmentResponse.updatedAt( entity.getUpdatedAt() );

        return adjustmentResponse.build();
    }

    @Override
    public WarehouseDtos.AdjustmentLineResponse toResponse(InventoryAdjustmentLineEntity entity) {
        if ( entity == null ) {
            return null;
        }

        WarehouseDtos.AdjustmentLineResponse.AdjustmentLineResponseBuilder adjustmentLineResponse = WarehouseDtos.AdjustmentLineResponse.builder();

        adjustmentLineResponse.id( entity.getId() );
        adjustmentLineResponse.productId( entity.getProductId() );
        adjustmentLineResponse.variantId( entity.getVariantId() );
        adjustmentLineResponse.quantityChange( entity.getQuantityChange() );
        adjustmentLineResponse.unitCost( entity.getUnitCost() );
        adjustmentLineResponse.notes( entity.getNotes() );

        return adjustmentLineResponse.build();
    }

    protected List<WarehouseDtos.TransferLineResponse> transferLineEntityListToTransferLineResponseList(List<TransferLineEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<WarehouseDtos.TransferLineResponse> list1 = new ArrayList<WarehouseDtos.TransferLineResponse>( list.size() );
        for ( TransferLineEntity transferLineEntity : list ) {
            list1.add( toResponse( transferLineEntity ) );
        }

        return list1;
    }

    protected List<WarehouseDtos.StockCountLineResponse> stockCountLineEntityListToStockCountLineResponseList(List<StockCountLineEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<WarehouseDtos.StockCountLineResponse> list1 = new ArrayList<WarehouseDtos.StockCountLineResponse>( list.size() );
        for ( StockCountLineEntity stockCountLineEntity : list ) {
            list1.add( toResponse( stockCountLineEntity ) );
        }

        return list1;
    }

    protected List<WarehouseDtos.AdjustmentLineResponse> inventoryAdjustmentLineEntityListToAdjustmentLineResponseList(List<InventoryAdjustmentLineEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<WarehouseDtos.AdjustmentLineResponse> list1 = new ArrayList<WarehouseDtos.AdjustmentLineResponse>( list.size() );
        for ( InventoryAdjustmentLineEntity inventoryAdjustmentLineEntity : list ) {
            list1.add( toResponse( inventoryAdjustmentLineEntity ) );
        }

        return list1;
    }
}
