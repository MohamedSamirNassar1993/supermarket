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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-11T02:20:56+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.18 (Microsoft)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public CategoryDtos.Response toResponse(CategoryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CategoryDtos.Response.ResponseBuilder response = CategoryDtos.Response.builder();

        response.id( entity.getId() );
        response.organizationId( entity.getOrganizationId() );
        response.parentId( entity.getParentId() );
        response.code( entity.getCode() );
        response.name( entity.getName() );
        response.description( entity.getDescription() );
        response.markupPercent( entity.getMarkupPercent() );
        response.active( entity.isActive() );
        response.createdAt( entity.getCreatedAt() );
        response.updatedAt( entity.getUpdatedAt() );

        return response.build();
    }

    @Override
    public BrandDtos.Response toResponse(BrandEntity entity) {
        if ( entity == null ) {
            return null;
        }

        BrandDtos.Response.ResponseBuilder response = BrandDtos.Response.builder();

        response.id( entity.getId() );
        response.organizationId( entity.getOrganizationId() );
        response.code( entity.getCode() );
        response.name( entity.getName() );
        response.description( entity.getDescription() );
        response.active( entity.isActive() );
        response.createdAt( entity.getCreatedAt() );
        response.updatedAt( entity.getUpdatedAt() );

        return response.build();
    }

    @Override
    public CategoryEntity toEntity(CategoryDtos.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setOrganizationId( request.getOrganizationId() );
        categoryEntity.setParentId( request.getParentId() );
        categoryEntity.setCode( request.getCode() );
        categoryEntity.setName( request.getName() );
        categoryEntity.setDescription( request.getDescription() );
        categoryEntity.setMarkupPercent( request.getMarkupPercent() );

        categoryEntity.setActive( true );

        return categoryEntity;
    }

    @Override
    public void updateCategory(CategoryDtos.UpdateRequest request, CategoryEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setParentId( request.getParentId() );
        entity.setName( request.getName() );
        entity.setDescription( request.getDescription() );
        entity.setMarkupPercent( request.getMarkupPercent() );
        if ( request.getActive() != null ) {
            entity.setActive( request.getActive() );
        }
    }

    @Override
    public BrandEntity toEntity(BrandDtos.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setOrganizationId( request.getOrganizationId() );
        brandEntity.setCode( request.getCode() );
        brandEntity.setName( request.getName() );
        brandEntity.setDescription( request.getDescription() );

        brandEntity.setActive( true );

        return brandEntity;
    }

    @Override
    public void updateBrand(BrandDtos.UpdateRequest request, BrandEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setName( request.getName() );
        entity.setDescription( request.getDescription() );
        if ( request.getActive() != null ) {
            entity.setActive( request.getActive() );
        }
    }

    @Override
    public ProductDtos.Response toResponse(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDtos.Response.ResponseBuilder response = ProductDtos.Response.builder();

        response.id( entity.getId() );
        response.organizationId( entity.getOrganizationId() );
        response.categoryId( entity.getCategoryId() );
        response.brandId( entity.getBrandId() );
        response.unitId( entity.getUnitId() );
        response.sku( entity.getSku() );
        response.name( entity.getName() );
        response.description( entity.getDescription() );
        response.basePrice( entity.getBasePrice() );
        response.costPrice( entity.getCostPrice() );
        response.taxRate( entity.getTaxRate() );
        response.trackInventory( entity.isTrackInventory() );
        response.trackExpiry( entity.isTrackExpiry() );
        response.reorderLevel( entity.getReorderLevel() );
        response.reorderQuantity( entity.getReorderQuantity() );
        response.active( entity.isActive() );
        response.variants( productVariantEntityListToVariantResponseList( entity.getVariants() ) );
        response.barcodes( productBarcodeEntityListToBarcodeResponseList( entity.getBarcodes() ) );
        response.images( productImageEntityListToImageResponseList( entity.getImages() ) );
        response.createdAt( entity.getCreatedAt() );
        response.updatedAt( entity.getUpdatedAt() );

        return response.build();
    }

    @Override
    public ProductDtos.VariantResponse toResponse(ProductVariantEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDtos.VariantResponse.VariantResponseBuilder variantResponse = ProductDtos.VariantResponse.builder();

        variantResponse.id( entity.getId() );
        variantResponse.productId( entity.getProductId() );
        variantResponse.sku( entity.getSku() );
        variantResponse.name( entity.getName() );
        Map<String, String> map = entity.getAttributes();
        if ( map != null ) {
            variantResponse.attributes( new LinkedHashMap<String, String>( map ) );
        }
        variantResponse.price( entity.getPrice() );
        variantResponse.costPrice( entity.getCostPrice() );
        variantResponse.active( entity.isActive() );

        return variantResponse.build();
    }

    @Override
    public ProductDtos.BarcodeResponse toResponse(ProductBarcodeEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDtos.BarcodeResponse.BarcodeResponseBuilder barcodeResponse = ProductDtos.BarcodeResponse.builder();

        barcodeResponse.id( entity.getId() );
        barcodeResponse.productId( entity.getProductId() );
        barcodeResponse.variantId( entity.getVariantId() );
        barcodeResponse.barcode( entity.getBarcode() );
        barcodeResponse.barcodeType( entity.getBarcodeType() );
        barcodeResponse.primaryBarcode( entity.isPrimaryBarcode() );

        return barcodeResponse.build();
    }

    @Override
    public ProductDtos.ImageResponse toResponse(ProductImageEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDtos.ImageResponse.ImageResponseBuilder imageResponse = ProductDtos.ImageResponse.builder();

        imageResponse.id( entity.getId() );
        imageResponse.productId( entity.getProductId() );
        imageResponse.variantId( entity.getVariantId() );
        imageResponse.url( entity.getUrl() );
        imageResponse.sortOrder( entity.getSortOrder() );
        imageResponse.primaryImage( entity.isPrimaryImage() );

        return imageResponse.build();
    }

    @Override
    public ProductDtos.PriceHistoryResponse toResponse(PriceHistoryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDtos.PriceHistoryResponse.PriceHistoryResponseBuilder priceHistoryResponse = ProductDtos.PriceHistoryResponse.builder();

        priceHistoryResponse.id( entity.getId() );
        priceHistoryResponse.productId( entity.getProductId() );
        priceHistoryResponse.variantId( entity.getVariantId() );
        priceHistoryResponse.oldPrice( entity.getOldPrice() );
        priceHistoryResponse.newPrice( entity.getNewPrice() );
        priceHistoryResponse.reason( entity.getReason() );
        priceHistoryResponse.effectiveAt( entity.getEffectiveAt() );
        priceHistoryResponse.changedBy( entity.getChangedBy() );

        return priceHistoryResponse.build();
    }

    @Override
    public ProductEntity toEntity(ProductDtos.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        ProductEntity productEntity = new ProductEntity();

        productEntity.setOrganizationId( request.getOrganizationId() );
        productEntity.setCategoryId( request.getCategoryId() );
        productEntity.setBrandId( request.getBrandId() );
        productEntity.setUnitId( request.getUnitId() );
        productEntity.setSku( request.getSku() );
        productEntity.setName( request.getName() );
        productEntity.setDescription( request.getDescription() );
        productEntity.setReorderLevel( request.getReorderLevel() );
        productEntity.setReorderQuantity( request.getReorderQuantity() );

        productEntity.setActive( true );
        productEntity.setTrackInventory( request.getTrackInventory() != null ? request.getTrackInventory() : true );
        productEntity.setTrackExpiry( request.getTrackExpiry() != null ? request.getTrackExpiry() : false );
        productEntity.setBasePrice( request.getBasePrice() != null ? request.getBasePrice() : java.math.BigDecimal.ZERO );
        productEntity.setCostPrice( request.getCostPrice() != null ? request.getCostPrice() : java.math.BigDecimal.ZERO );
        productEntity.setTaxRate( request.getTaxRate() != null ? request.getTaxRate() : java.math.BigDecimal.ZERO );

        return productEntity;
    }

    @Override
    public void updateProduct(ProductDtos.UpdateRequest request, ProductEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setCategoryId( request.getCategoryId() );
        entity.setBrandId( request.getBrandId() );
        entity.setUnitId( request.getUnitId() );
        entity.setName( request.getName() );
        entity.setDescription( request.getDescription() );
        entity.setBasePrice( request.getBasePrice() );
        entity.setCostPrice( request.getCostPrice() );
        entity.setTaxRate( request.getTaxRate() );
        if ( request.getTrackInventory() != null ) {
            entity.setTrackInventory( request.getTrackInventory() );
        }
        if ( request.getTrackExpiry() != null ) {
            entity.setTrackExpiry( request.getTrackExpiry() );
        }
        entity.setReorderLevel( request.getReorderLevel() );
        entity.setReorderQuantity( request.getReorderQuantity() );
        if ( request.getActive() != null ) {
            entity.setActive( request.getActive() );
        }
    }

    protected List<ProductDtos.VariantResponse> productVariantEntityListToVariantResponseList(List<ProductVariantEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductDtos.VariantResponse> list1 = new ArrayList<ProductDtos.VariantResponse>( list.size() );
        for ( ProductVariantEntity productVariantEntity : list ) {
            list1.add( toResponse( productVariantEntity ) );
        }

        return list1;
    }

    protected List<ProductDtos.BarcodeResponse> productBarcodeEntityListToBarcodeResponseList(List<ProductBarcodeEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductDtos.BarcodeResponse> list1 = new ArrayList<ProductDtos.BarcodeResponse>( list.size() );
        for ( ProductBarcodeEntity productBarcodeEntity : list ) {
            list1.add( toResponse( productBarcodeEntity ) );
        }

        return list1;
    }

    protected List<ProductDtos.ImageResponse> productImageEntityListToImageResponseList(List<ProductImageEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductDtos.ImageResponse> list1 = new ArrayList<ProductDtos.ImageResponse>( list.size() );
        for ( ProductImageEntity productImageEntity : list ) {
            list1.add( toResponse( productImageEntity ) );
        }

        return list1;
    }
}
