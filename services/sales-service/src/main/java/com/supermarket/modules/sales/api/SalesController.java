package com.supermarket.modules.sales.api;

import com.supermarket.modules.sales.application.SalesService;
import com.supermarket.modules.sales.application.dto.CreateSalesInvoiceRequest;
import com.supermarket.modules.sales.application.dto.SalesInvoiceResponse;
import com.supermarket.modules.sales.application.dto.SalesReturnRequest;
import com.supermarket.modules.sales.application.dto.SalesReturnResponse;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping("/invoices")
    public ResponseEntity<ApiResponse<SalesInvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateSalesInvoiceRequest request) {
        var result = salesService.createInvoice(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/invoices/{id}")
    public ApiResponse<SalesInvoiceResponse> getInvoice(@PathVariable UUID id) {
        return ApiResponse.success(salesService.getInvoice(requireOrganization(), id));
    }

    @PostMapping("/returns")
    public ResponseEntity<ApiResponse<SalesReturnResponse>> createReturn(
            @Valid @RequestBody SalesReturnRequest request) {
        var result = salesService.createReturn(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    private UUID requireOrganization() {
        UUID orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "X-Organization-Id header is required");
        }
        return orgId;
    }
}
