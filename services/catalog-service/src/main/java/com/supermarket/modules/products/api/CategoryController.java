package com.supermarket.modules.products.api;

import com.supermarket.modules.products.application.dto.CategoryDtos;
import com.supermarket.modules.products.application.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<PageResponse<CategoryDtos.Response>> list(
            @RequestParam UUID organizationId,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(categoryService.list(organizationId, active, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDtos.Response> get(
            @RequestParam UUID organizationId,
            @PathVariable UUID id) {
        return ApiResponse.success(categoryService.get(organizationId, id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDtos.Response>> create(@Valid @RequestBody CategoryDtos.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryDtos.Response> update(
            @RequestParam UUID organizationId,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryDtos.UpdateRequest request) {
        return ApiResponse.success(categoryService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestParam UUID organizationId, @PathVariable UUID id) {
        categoryService.delete(organizationId, id);
        return ApiResponse.success("Category deactivated", null);
    }
}
