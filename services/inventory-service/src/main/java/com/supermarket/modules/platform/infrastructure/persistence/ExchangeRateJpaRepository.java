package com.supermarket.modules.platform.infrastructure.persistence;

import com.supermarket.modules.platform.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateJpaRepository extends JpaRepository<ExchangeRate, UUID> {

    @Query("""
            SELECT er FROM ExchangeRate er
            WHERE er.fromCurrencyId = :fromId AND er.toCurrencyId = :toId
              AND er.effectiveDate <= :date
            ORDER BY er.effectiveDate DESC
            """)
    List<ExchangeRate> findRatesUpToDate(
            @Param("fromId") UUID fromId,
            @Param("toId") UUID toId,
            @Param("date") LocalDate date);
}
