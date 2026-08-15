package com.supermarket.modules.purchases.api;

import com.supermarket.modules.purchases.application.PurchaseService;
import com.supermarket.modules.purchases.application.dto.GoodsReceiptRequest;
import com.supermarket.modules.purchases.application.dto.GoodsReceiptResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseOrderRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseOrderResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseQuotationRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseQuotationResponse;
import com.supermarket.modules.purchases.application.dto.PurchaseReturnRequest;
import com.supermarket.modules.purchases.application.dto.PurchaseReturnResponse;
import com.supermarket.modules.purchases.application.dto.SupplierPaymentRequest;
import com.supermarket.modules.purchases.application.dto.SupplierPaymentResponse;
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
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/quotations")
    public ResponseEntity<ApiResponse<PurchaseQuotationResponse>> createQuotation(
            @Valid @RequestBody PurchaseQuotationRequest request) {
        var result = purchaseService.createQuotation(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/quotations/{id}")
    public ApiResponse<PurchaseQuotationResponse> getQuotation(@PathVariable UUID id) {
        return ApiResponse.success(purchaseService.getQuotation(requireOrganization(), id));
    }

    @PostMapping("/quotations/{id}/approve")
    public ApiResponse<PurchaseQuotationResponse> approveQuotation(@PathVariable UUID id) {
        return ApiResponse.success(purchaseService.approveQuotation(requireOrganization(), id));
    }

    @PostMapping("/quotations/{id}/convert-to-order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> convertToOrder(@PathVariable UUID id) {
        var result = purchaseService.createOrderFromQuotation(requireOrganization(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {
        var result = purchaseService.createOrder(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/orders/{id}")
    public ApiResponse<PurchaseOrderResponse> getOrder(@PathVariable UUID id) {
        return ApiResponse.success(purchaseService.getOrder(requireOrganization(), id));
    }

    @PostMapping("/orders/{id}/submit")
    public ApiResponse<PurchaseOrderResponse> submitOrder(@PathVariable UUID id) {
        return ApiResponse.success(purchaseService.submitOrder(requireOrganization(), id));
    }

    @PostMapping("/orders/{id}/approve")
    public ApiResponse<PurchaseOrderResponse> approveOrder(@PathVariable UUID id) {
        return ApiResponse.success(purchaseService.approveOrder(requireOrganization(), id));
    }

    @PostMapping("/receipts")
    public ResponseEntity<ApiResponse<GoodsReceiptResponse>> receiveGoods(
            @Valid @RequestBody GoodsReceiptRequest request) {
        var result = purchaseService.receiveGoods(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<SupplierPaymentResponse>> recordPayment(
            @Valid @RequestBody SupplierPaymentRequest request) {
        var result = purchaseService.recordPayment(requireOrganization(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/returns")
    public ResponseEntity<ApiResponse<PurchaseReturnResponse>> createReturn(
            @Valid @RequestBody PurchaseReturnRequest request) {
        var result = purchaseService.createReturn(requireOrganization(), request);
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
