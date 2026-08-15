package com.supermarket.modules.products.infrastructure.persistence;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductEntity {

    private UUID id;
    private UUID organizationId;
    private UUID categoryId;
    private UUID brandId;
    private UUID unitId;
    private String sku;
    private String name;
    private String description;
    private BigDecimal basePrice = BigDecimal.ZERO;
    private BigDecimal costPrice = BigDecimal.ZERO;
    private BigDecimal taxRate = BigDecimal.ZERO;
    private boolean trackInventory = true;
    private boolean trackExpiry = false;
    private BigDecimal reorderLevel;
    private boolean active = true;

    public boolean isActive() {
        return active;
    }
}
