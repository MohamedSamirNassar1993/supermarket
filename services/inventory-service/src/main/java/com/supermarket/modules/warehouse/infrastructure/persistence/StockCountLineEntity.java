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
@Table(name = "stock_count_lines")
@Getter
@Setter
public class StockCountLineEntity extends BaseEntity {

    @Column(name = "stock_count_id", nullable = false)
    private UUID stockCountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_count_id", insertable = false, updatable = false)
    private StockCountEntity stockCount;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "system_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal systemQuantity = BigDecimal.ZERO;

    @Column(name = "counted_quantity", precision = 19, scale = 4)
    private BigDecimal countedQuantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal variance;
}
