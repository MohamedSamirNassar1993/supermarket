package com.supermarket.modules.inventory.infrastructure.persistence;

import com.supermarket.modules.inventory.domain.ValuationMethod;
import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "organization_settings")
@Getter
@Setter
public class OrganizationSettingsEntity extends AuditableEntity {

    @Column(name = "organization_id", nullable = false, unique = true)
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "valuation_method", nullable = false, length = 30)
    private ValuationMethod valuationMethod = ValuationMethod.FIFO;

    @Column(name = "allow_negative_stock", nullable = false)
    private boolean allowNegativeStock = false;

    @Column(name = "low_stock_alert_enabled", nullable = false)
    private boolean lowStockAlertEnabled = true;

    @Column(name = "expiry_alert_days", nullable = false)
    private int expiryAlertDays = 30;
}
