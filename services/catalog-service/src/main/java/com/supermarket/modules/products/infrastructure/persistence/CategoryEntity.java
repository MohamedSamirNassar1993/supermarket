package com.supermarket.modules.products.infrastructure.persistence;

import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity extends AuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "markup_percent", precision = 7, scale = 4)
    private BigDecimal markupPercent;

    @Column(nullable = false)
    private boolean active = true;
}
