package com.supermarket.modules.barcode.domain;

import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "label_templates")
@Getter
@Setter
public class LabelTemplate extends AuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "label_type", nullable = false, length = 30)
    private String labelType = "PRODUCT";

    @Column(name = "width_mm", nullable = false, precision = 6, scale = 2)
    private BigDecimal widthMm = new BigDecimal("50");

    @Column(name = "height_mm", nullable = false, precision = 6, scale = 2)
    private BigDecimal heightMm = new BigDecimal("30");

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "template_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> templateData;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
