package com.supermarket.modules.sales.api;

import com.supermarket.modules.sales.application.SalesService;
import com.supermarket.modules.sales.application.dto.PosSyncRequest;
import com.supermarket.modules.sales.application.dto.PosSyncResponse;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pos/sync")
@RequiredArgsConstructor
public class PosSyncController {

    private final SalesService salesService;

    @PostMapping
    public ResponseEntity<ApiResponse<PosSyncResponse>> sync(@Valid @RequestBody PosSyncRequest request) {
        var result = salesService.processPosSync(requireOrganization(), request);
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
