package com.supermarket.modules.platform.api;

import com.supermarket.modules.platform.application.AuditService;
import com.supermarket.modules.platform.application.ExchangeRateService;
import com.supermarket.modules.platform.domain.AuditLog;
import com.supermarket.modules.platform.domain.Currency;
import com.supermarket.modules.platform.domain.ExchangeRate;
import com.supermarket.modules.platform.infrastructure.persistence.BranchEntity;
import com.supermarket.modules.platform.infrastructure.persistence.BranchRepository;
import com.supermarket.modules.platform.infrastructure.persistence.CurrencyJpaRepository;
import com.supermarket.modules.platform.infrastructure.persistence.OrganizationEntity;
import com.supermarket.modules.platform.infrastructure.persistence.OrganizationRepository;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final OrganizationRepository organizationRepository;
    private final BranchRepository branchRepository;
    private final CurrencyJpaRepository currencyRepository;
    private final ExchangeRateService exchangeRateService;
    private final AuditService auditService;

    @GetMapping("/organizations/{id}")
    public ApiResponse<OrganizationEntity> getOrganization(@PathVariable UUID id) {
        return ApiResponse.success(organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found: " + id)));
    }

    @GetMapping("/organizations/{orgId}/branches")
    public ApiResponse<List<BranchEntity>> listBranches(@PathVariable UUID orgId) {
        return ApiResponse.success(branchRepository.findByOrganizationIdAndActiveTrue(orgId));
    }

    @GetMapping("/currencies")
    public ApiResponse<List<Currency>> listCurrencies() {
        return ApiResponse.success(currencyRepository.findAll());
    }

    @GetMapping("/exchange-rates")
    public ApiResponse<List<ExchangeRate>> listExchangeRates() {
        return ApiResponse.success(exchangeRateService.listRates());
    }

    @PostMapping("/exchange-rates")
    public ApiResponse<ExchangeRate> createExchangeRate(@Valid @RequestBody ExchangeRate rate) {
        return ApiResponse.success(exchangeRateService.createRate(rate));
    }

    @GetMapping("/exchange-rates/convert")
    public ApiResponse<BigDecimal> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(exchangeRateService.convert(from, to, amount, date));
    }

    @GetMapping("/audit-logs")
    public ApiResponse<PageResponse<AuditLog>> listAuditLogs(@PageableDefault(size = 50) Pageable pageable) {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(PageResponse.from(auditService.listByOrganization(orgId, pageable)));
    }
}
