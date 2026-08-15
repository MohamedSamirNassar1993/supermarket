package com.supermarket.modules.products.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductEntity {

    private UUID id;
    private UUID organizationId;
    private String sku;
    private String name;
    private BigDecimal reorderLevel;
    private boolean trackInventory = true;
    private boolean active = true;
}
