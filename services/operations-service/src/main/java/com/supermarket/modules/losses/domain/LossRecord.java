package com.supermarket.modules.losses.domain;

import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loss_records")
@Getter
@Setter
public class LossRecord extends AuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "loss_number", nullable = false, length = 50)
    private String lossNumber;

    @Column(name = "loss_type", nullable = false, length = 30)
    private String lossType;

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "total_loss", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalLoss;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "loss_date", nullable = false)
    private LocalDate lossDate;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "RECORDED";

    @Column(name = "stock_batch_id")
    private UUID stockBatchId;
}
