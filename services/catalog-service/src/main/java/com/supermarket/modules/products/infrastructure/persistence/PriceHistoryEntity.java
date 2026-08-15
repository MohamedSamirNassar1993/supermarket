package com.supermarket.modules.products.infrastructure.persistence;

import com.supermarket.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_history")
@Getter
@Setter
public class PriceHistoryEntity extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "old_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal oldPrice;

    @Column(name = "new_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal newPrice;

    @Column(length = 255)
    private String reason;

    @Column(name = "effective_at", nullable = false)
    private Instant effectiveAt = Instant.now();

    @Column(name = "changed_by")
    private String changedBy;
}
