package com.supermarket.modules.products.application.mapper;

import com.supermarket.modules.products.application.dto.BrandDtos;
import com.supermarket.modules.products.application.dto.CategoryDtos;
import com.supermarket.modules.products.application.dto.ProductDtos;
import com.supermarket.modules.products.infrastructure.persistence.BrandEntity;
import com.supermarket.modules.products.infrastructure.persistence.CategoryEntity;
import com.supermarket.modules.products.infrastructure.persistence.PriceHistoryEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductBarcodeEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductImageEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductVariantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    CategoryDtos.Response toResponse(CategoryEntity entity);

    BrandDtos.Response toResponse(BrandEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    CategoryEntity toEntity(CategoryDtos.CreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateCategory(CategoryDtos.UpdateRequest request, @MappingTarget CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    BrandEntity toEntity(BrandDtos.CreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateBrand(BrandDtos.UpdateRequest request, @MappingTarget BrandEntity entity);

    ProductDtos.Response toResponse(ProductEntity entity);

    ProductDtos.VariantResponse toResponse(ProductVariantEntity entity);

    ProductDtos.BarcodeResponse toResponse(ProductBarcodeEntity entity);

    ProductDtos.ImageResponse toResponse(ProductImageEntity entity);

    ProductDtos.PriceHistoryResponse toResponse(PriceHistoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "barcodes", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "trackInventory", expression = "java(request.getTrackInventory() != null ? request.getTrackInventory() : true)")
    @Mapping(target = "trackExpiry", expression = "java(request.getTrackExpiry() != null ? request.getTrackExpiry() : false)")
    @Mapping(target = "basePrice", expression = "java(request.getBasePrice() != null ? request.getBasePrice() : java.math.BigDecimal.ZERO)")
    @Mapping(target = "costPrice", expression = "java(request.getCostPrice() != null ? request.getCostPrice() : java.math.BigDecimal.ZERO)")
    @Mapping(target = "taxRate", expression = "java(request.getTaxRate() != null ? request.getTaxRate() : java.math.BigDecimal.ZERO)")
    ProductEntity toEntity(ProductDtos.CreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "barcodes", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateProduct(ProductDtos.UpdateRequest request, @MappingTarget ProductEntity entity);
}
