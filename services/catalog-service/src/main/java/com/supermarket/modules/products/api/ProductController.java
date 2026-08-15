package com.supermarket.modules.products.api;

import com.supermarket.modules.products.application.dto.ProductDtos;
import com.supermarket.modules.products.application.service.DynamicPricingService;
import com.supermarket.modules.products.application.service.ProductService;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final DynamicPricingService dynamicPricingService;

    @GetMapping
    public ApiResponse<PageResponse<ProductDtos.Response>> list(
            @RequestParam UUID organizationId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(productService.list(organizationId, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDtos.Response> get(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(productService.get(organizationId, id));
    }

    @GetMapping("/by-barcode/{barcode}")
    public ApiResponse<ProductDtos.Response> getByBarcode(@PathVariable String barcode) {
        return ApiResponse.success(productService.getByBarcode(barcode));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDtos.Response>> create(@Valid @RequestBody ProductDtos.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDtos.Response> update(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDtos.UpdateRequest request) {
        return ApiResponse.success(productService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestParam UUID organizationId, @PathVariable UUID id) {
        productService.delete(organizationId, id);
        return ApiResponse.success("Product deactivated", null);
    }

    @PutMapping("/{id}/price")
    public ApiResponse<ProductDtos.Response> updatePrice(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDtos.PriceUpdateRequest request) {
        return ApiResponse.success(productService.updatePrice(organizationId, id, request));
    }

    @GetMapping("/{id}/price-history")
    public ApiResponse<PageResponse<ProductDtos.PriceHistoryResponse>> priceHistory(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID variantId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(productService.priceHistory(id, variantId, pageable)));
    }

    @PostMapping("/{id}/variants")
    public ResponseEntity<ApiResponse<ProductDtos.VariantResponse>> addVariant(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDtos.VariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.addVariant(organizationId, id, request)));
    }

    @PostMapping("/{id}/barcodes")
    public ResponseEntity<ApiResponse<ProductDtos.BarcodeResponse>> addBarcode(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDtos.BarcodeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.addBarcode(organizationId, id, request)));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<ProductDtos.ImageResponse>> addImage(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDtos.ImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.addImage(organizationId, id, request)));
    }

    @PostMapping("/dynamic-price")
    public ApiResponse<ProductDtos.DynamicPriceResponse> dynamicPrice(@Valid @RequestBody ProductDtos.DynamicPriceRequest request) {
        return ApiResponse.success(dynamicPricingService.calculate(request));
    }
}
