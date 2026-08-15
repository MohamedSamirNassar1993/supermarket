package com.supermarket.modules.platform.domain;

import com.supermarket.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "currencies")
@Getter
@Setter
public class Currency extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 3)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "symbol", length = 10)
    private String symbol;

    @Column(name = "decimal_places", nullable = false)
    private short decimalPlaces = 2;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
