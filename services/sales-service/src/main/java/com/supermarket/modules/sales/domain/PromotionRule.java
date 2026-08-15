package com.supermarket.modules.sales.domain;

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

@Getter
@Setter
@Entity
@Table(name = "promotion_rules")
public class PromotionRule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "rule_type", nullable = false, length = 30)
    private String ruleType;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "min_quantity", precision = 19, scale = 4)
    private BigDecimal minQuantity;

    @Column(name = "min_amount", precision = 19, scale = 4)
    private BigDecimal minAmount;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountValue;

    @Column(name = "free_product_id")
    private UUID freeProductId;

    @Column(name = "free_quantity", precision = 19, scale = 4)
    private BigDecimal freeQuantity;
}
