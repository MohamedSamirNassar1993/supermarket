package com.supermarket.modules.inventory.infrastructure.persistence;

import com.supermarket.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "batch_allocations")
@Getter
@Setter
public class BatchAllocationEntity extends BaseEntity {

    @Column(name = "movement_id", nullable = false)
    private UUID movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_id", insertable = false, updatable = false)
    private InventoryMovementEntity movement;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", insertable = false, updatable = false)
    private StockBatchEntity batch;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "total_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalCost;
}
