package com.supermarket.modules.customers.api;

import com.supermarket.modules.customers.application.CustomerService;
import com.supermarket.modules.customers.application.dto.CustomerGroupRequest;
import com.supermarket.modules.customers.application.dto.CustomerGroupResponse;
import com.supermarket.modules.customers.application.dto.CustomerRequest;
import com.supermarket.modules.customers.application.dto.CustomerResponse;
import com.supermarket.modules.customers.application.dto.CustomerStatementLineResponse;
import com.supermarket.modules.customers.application.dto.InstallmentPlanRequest;
import com.supermarket.modules.customers.application.dto.InstallmentPlanResponse;
import com.supermarket.modules.customers.application.dto.LoyaltyAdjustRequest;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.api.PageResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ApiResponse<PageResponse<CustomerResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(customerService.list(requireOrganization(), pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(customerService.get(requireOrganization(), id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest request) {
        var result = customerService.create(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.success(customerService.update(requireOrganization(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        customerService.delete(requireOrganization(), id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/statement")
    public ApiResponse<PageResponse<CustomerStatementLineResponse>> statement(
            @PathVariable UUID id,
            @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(customerService.getStatement(requireOrganization(), id, pageable));
    }

    @PostMapping("/{id}/loyalty/adjust")
    public ApiResponse<CustomerResponse> adjustLoyalty(@PathVariable UUID id,
                                                        @Valid @RequestBody LoyaltyAdjustRequest request) {
        return ApiResponse.success(customerService.adjustLoyalty(requireOrganization(), id, request));
    }

    @PostMapping("/{id}/installments")
    public ResponseEntity<ApiResponse<InstallmentPlanResponse>> createInstallments(
            @PathVariable UUID id,
            @Valid @RequestBody InstallmentPlanRequest request) {
        var result = customerService.createInstallmentPlan(requireOrganization(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/groups")
    public ApiResponse<List<CustomerGroupResponse>> listGroups() {
        return ApiResponse.success(customerService.listGroups(requireOrganization()));
    }

    @PostMapping("/groups")
    public ResponseEntity<ApiResponse<CustomerGroupResponse>> createGroup(
            @Valid @RequestBody CustomerGroupRequest request) {
        var result = customerService.createGroup(requireOrganization(), request);
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
