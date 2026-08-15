package com.supermarket.modules.platform.infrastructure.persistence;

import com.supermarket.modules.platform.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyJpaRepository extends JpaRepository<Currency, UUID> {
    Optional<Currency> findByCode(String code);
}
