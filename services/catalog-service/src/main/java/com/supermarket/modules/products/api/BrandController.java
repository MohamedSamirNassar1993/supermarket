package com.supermarket.modules.products.api;

import com.supermarket.modules.products.application.dto.BrandDtos;
import com.supermarket.modules.products.application.service.BrandService;
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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ApiResponse<PageResponse<BrandDtos.Response>> list(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(brandService.list(organizationId, active, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandDtos.Response> get(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(brandService.get(organizationId, id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BrandDtos.Response>> create(@Valid @RequestBody BrandDtos.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(brandService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandDtos.Response> update(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody BrandDtos.UpdateRequest request) {
        return ApiResponse.success(brandService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestParam UUID organizationId, @PathVariable UUID id) {
        brandService.delete(organizationId, id);
        return ApiResponse.success("Brand deactivated", null);
    }
}
