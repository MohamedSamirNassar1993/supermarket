package com.supermarket.modules.suppliers.api;

import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
import com.supermarket.modules.suppliers.application.SupplierService;
import com.supermarket.modules.suppliers.application.dto.SupplierBalanceResponse;
import com.supermarket.modules.suppliers.application.dto.SupplierRatingRequest;
import com.supermarket.modules.suppliers.application.dto.SupplierRequest;
import com.supermarket.modules.suppliers.application.dto.SupplierResponse;
import com.supermarket.modules.suppliers.application.dto.SupplierStatementLineResponse;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ApiResponse<PageResponse<SupplierResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID orgId = requireOrganization();
        return ApiResponse.success(supplierService.list(orgId, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<SupplierResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(supplierService.get(requireOrganization(), id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse created = supplierService.create(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    public ApiResponse<SupplierResponse> update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        return ApiResponse.success(supplierService.update(requireOrganization(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        supplierService.delete(requireOrganization(), id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/balance")
    public ApiResponse<SupplierBalanceResponse> balance(@PathVariable UUID id) {
        return ApiResponse.success(supplierService.getBalance(requireOrganization(), id));
    }

    @GetMapping("/{id}/statement")
    public ApiResponse<PageResponse<SupplierStatementLineResponse>> statement(
            @PathVariable UUID id,
            @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(supplierService.getStatement(requireOrganization(), id, pageable));
    }

    @PutMapping("/{id}/rating")
    public ApiResponse<SupplierResponse> rating(@PathVariable UUID id,
                                                 @Valid @RequestBody SupplierRatingRequest request) {
        return ApiResponse.success(supplierService.updateRating(requireOrganization(), id, request));
    }

    private UUID requireOrganization() {
        UUID orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "X-Organization-Id header is required");
        }
        return orgId;
    }
}
