package com.supermarket.modules.platform.application;

import com.supermarket.modules.platform.domain.Currency;
import com.supermarket.modules.platform.domain.ExchangeRate;
import com.supermarket.modules.platform.infrastructure.persistence.CurrencyJpaRepository;
import com.supermarket.modules.platform.infrastructure.persistence.ExchangeRateJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final CurrencyJpaRepository currencyRepository;
    private final ExchangeRateJpaRepository exchangeRateRepository;

    @Transactional(readOnly = true)
    public BigDecimal convert(String fromCode, String toCode, BigDecimal amount, LocalDate date) {
        if (fromCode.equalsIgnoreCase(toCode)) {
            return amount;
        }
        Currency from = currencyRepository.findByCode(fromCode.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found: " + fromCode));
        Currency to = currencyRepository.findByCode(toCode.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found: " + toCode));
        List<ExchangeRate> rates = exchangeRateRepository.findRatesUpToDate(
                from.getId(), to.getId(), date != null ? date : LocalDate.now());
        ExchangeRate rate = rates.stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "No exchange rate for " + fromCode + " -> " + toCode));
        return amount.multiply(rate.getRate())
                .setScale(to.getDecimalPlaces(), RoundingMode.HALF_UP);
    }

    @Transactional
    public ExchangeRate createRate(ExchangeRate rate) {
        if (rate.getEffectiveDate() == null) {
            rate.setEffectiveDate(LocalDate.now());
        }
        return exchangeRateRepository.save(rate);
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> listRates() {
        return exchangeRateRepository.findAll();
    }
}
