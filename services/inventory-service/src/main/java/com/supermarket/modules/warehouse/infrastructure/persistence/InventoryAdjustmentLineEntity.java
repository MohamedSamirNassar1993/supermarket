package com.supermarket.modules.warehouse.infrastructure.persistence;

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
@Table(name = "inventory_adjustment_lines")
@Getter
@Setter
public class InventoryAdjustmentLineEntity extends BaseEntity {

    @Column(name = "adjustment_id", nullable = false)
    private UUID adjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjustment_id", insertable = false, updatable = false)
    private InventoryAdjustmentEntity adjustment;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "quantity_change", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityChange;

    @Column(name = "unit_cost", precision = 19, scale = 4)
    private BigDecimal unitCost;

    private String notes;
}
