package com.supermarket.modules.platform.domain;

import com.supermarket.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
public class ExchangeRate extends BaseEntity {

    @Column(name = "from_currency_id", nullable = false)
    private UUID fromCurrencyId;

    @Column(name = "to_currency_id", nullable = false)
    private UUID toCurrencyId;

    @Column(name = "rate", nullable = false, precision = 19, scale = 8)
    private BigDecimal rate;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "source", length = 100)
    private String source;
}
