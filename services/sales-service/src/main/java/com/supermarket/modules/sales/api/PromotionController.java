package com.supermarket.modules.sales.api;

import com.supermarket.modules.sales.application.PromotionService;
import com.supermarket.modules.sales.application.dto.PromotionRequest;
import com.supermarket.modules.sales.application.dto.PromotionResponse;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.ErrorCode;
import com.supermarket.shared.domain.BusinessException;
import com.supermarket.shared.domain.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ApiResponse<List<PromotionResponse>> list() {
        return ApiResponse.success(promotionService.list(requireOrganization()));
    }

    @GetMapping("/{id}")
    public ApiResponse<PromotionResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(promotionService.get(requireOrganization(), id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponse>> create(@Valid @RequestBody PromotionRequest request) {
        var result = promotionService.create(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable UUID id) {
        promotionService.deactivate(requireOrganization(), id);
        return ApiResponse.success(null);
    }

    private UUID requireOrganization() {
        UUID orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "X-Organization-Id header is required");
        }
        return orgId;
    }
}
