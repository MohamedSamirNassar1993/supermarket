package com.supermarket.modules.products.application.service;

import com.supermarket.modules.platform.application.OrganizationGuard;
import com.supermarket.modules.products.application.dto.ProductDtos;
import com.supermarket.modules.products.application.mapper.ProductMapper;
import com.supermarket.modules.products.domain.BarcodeType;
import com.supermarket.modules.products.infrastructure.persistence.CategoryRepository;
import com.supermarket.modules.products.infrastructure.persistence.PriceHistoryEntity;
import com.supermarket.modules.products.infrastructure.persistence.PriceHistoryRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductBarcodeEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductBarcodeRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductImageEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductImageRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductVariantEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductVariantRepository;
import com.supermarket.modules.products.infrastructure.persistence.UnitRepository;
import com.supermarket.shared.exception.BusinessRuleException;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductBarcodeRepository barcodeRepository;
    private final ProductImageRepository imageRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductMapper productMapper;
    private final OrganizationGuard organizationGuard;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public Page<ProductDtos.Response> list(UUID organizationId, Pageable pageable) {
        organizationGuard.requireOrganization(organizationId);
        return productRepository.findByOrganizationId(organizationId, pageable)
                .map(this::toDetailedResponse);
    }

    @Transactional(readOnly = true)
    public ProductDtos.Response get(UUID organizationId, UUID id) {
        return toDetailedResponse(findProduct(organizationId, id));
    }

    @Transactional(readOnly = true)
    public ProductDtos.Response getByBarcode(String barcode) {
        ProductBarcodeEntity barcodeEntity = barcodeRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product barcode not found: " + barcode));
        ProductEntity product = findProduct(
                productRepository.findById(barcodeEntity.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", barcodeEntity.getProductId()))
                        .getOrganizationId(),
                barcodeEntity.getProductId());
        return toDetailedResponse(product);
    }

    @Transactional
    public ProductDtos.Response create(ProductDtos.CreateRequest request) {
        organizationGuard.requireOrganization(request.getOrganizationId());
        validateReferences(request.getOrganizationId(), request.getCategoryId(), request.getUnitId());
        if (productRepository.existsByOrganizationIdAndSku(request.getOrganizationId(), request.getSku())) {
            throw new BusinessRuleException("Product SKU already exists: " + request.getSku());
        }
        ProductEntity entity = productMapper.toEntity(request);
        return toDetailedResponse(productRepository.save(entity));
    }

    @Transactional
    public ProductDtos.Response update(UUID organizationId, UUID id, ProductDtos.UpdateRequest request) {
        ProductEntity entity = findProduct(organizationId, id);
        if (request.getCategoryId() != null) {
            categoryService.findEntity(organizationId, request.getCategoryId());
        }
        if (request.getUnitId() != null) {
            unitRepository.findByIdAndOrganizationId(request.getUnitId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", request.getUnitId()));
        }
        BigDecimal oldPrice = entity.getBasePrice();
        productMapper.updateProduct(request, entity);
        if (request.getBasePrice() != null && request.getBasePrice().compareTo(oldPrice) != 0) {
            recordPriceChange(entity.getId(), null, oldPrice, request.getBasePrice(), "Product update");
        }
        return toDetailedResponse(productRepository.save(entity));
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        ProductEntity entity = findProduct(organizationId, id);
        entity.setActive(false);
        productRepository.save(entity);
    }

    @Transactional
    public ProductDtos.Response updatePrice(UUID organizationId, UUID id, ProductDtos.PriceUpdateRequest request) {
        ProductEntity product = findProduct(organizationId, id);
        if (request.getVariantId() != null) {
            ProductVariantEntity variant = variantRepository.findByIdAndProductId(request.getVariantId(), id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product variant", request.getVariantId()));
            BigDecimal oldPrice = variant.getPrice() != null ? variant.getPrice() : product.getBasePrice();
            variant.setPrice(request.getNewPrice());
            variantRepository.save(variant);
            recordPriceChange(id, variant.getId(), oldPrice, request.getNewPrice(), request.getReason());
        } else {
            BigDecimal oldPrice = product.getBasePrice();
            product.setBasePrice(request.getNewPrice());
            productRepository.save(product);
            recordPriceChange(id, null, oldPrice, request.getNewPrice(), request.getReason());
        }
        return toDetailedResponse(findProduct(organizationId, id));
    }

    @Transactional(readOnly = true)
    public Page<ProductDtos.PriceHistoryResponse> priceHistory(UUID productId, UUID variantId, Pageable pageable) {
        Page<PriceHistoryEntity> page = variantId == null
                ? priceHistoryRepository.findByProductIdOrderByEffectiveAtDesc(productId, pageable)
                : priceHistoryRepository.findByProductIdAndVariantIdOrderByEffectiveAtDesc(productId, variantId, pageable);
        return page.map(productMapper::toResponse);
    }

    @Transactional
    public ProductDtos.VariantResponse addVariant(UUID organizationId, UUID productId, ProductDtos.VariantRequest request) {
        ProductEntity product = findProduct(organizationId, productId);
        if (variantRepository.existsByProductIdAndSku(productId, request.getSku())) {
            throw new BusinessRuleException("Variant SKU already exists: " + request.getSku());
        }
        ProductVariantEntity variant = new ProductVariantEntity();
        variant.setProductId(productId);
        variant.setProduct(product);
        variant.setSku(request.getSku());
        variant.setName(request.getName());
        variant.setAttributes(request.getAttributes());
        variant.setPrice(request.getPrice());
        variant.setCostPrice(request.getCostPrice());
        if (request.getActive() != null) {
            variant.setActive(request.getActive());
        }
        return productMapper.toResponse(variantRepository.save(variant));
    }

    @Transactional
    public ProductDtos.BarcodeResponse addBarcode(UUID organizationId, UUID productId, ProductDtos.BarcodeRequest request) {
        findProduct(organizationId, productId);
        if (barcodeRepository.existsByBarcode(request.getBarcode())) {
            throw new BusinessRuleException("Barcode already exists: " + request.getBarcode());
        }
        ProductBarcodeEntity barcode = new ProductBarcodeEntity();
        barcode.setProductId(productId);
        barcode.setVariantId(request.getVariantId());
        barcode.setBarcode(request.getBarcode());
        barcode.setBarcodeType(request.getBarcodeType() != null ? request.getBarcodeType() : BarcodeType.EAN13);
        barcode.setPrimaryBarcode(Boolean.TRUE.equals(request.getPrimaryBarcode()));
        return productMapper.toResponse(barcodeRepository.save(barcode));
    }

    @Transactional
    public ProductDtos.ImageResponse addImage(UUID organizationId, UUID productId, ProductDtos.ImageRequest request) {
        findProduct(organizationId, productId);
        ProductImageEntity image = new ProductImageEntity();
        image.setProductId(productId);
        image.setVariantId(request.getVariantId());
        image.setUrl(request.getUrl());
        image.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        image.setPrimaryImage(Boolean.TRUE.equals(request.getPrimaryImage()));
        return productMapper.toResponse(imageRepository.save(image));
    }

    private ProductEntity findProduct(UUID organizationId, UUID id) {
        organizationGuard.requireOrganization(organizationId);
        return productRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private void validateReferences(UUID organizationId, UUID categoryId, UUID unitId) {
        if (categoryId != null) {
            categoryRepository.findByIdAndOrganizationId(categoryId, organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        }
        unitRepository.findByIdAndOrganizationId(unitId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", unitId));
    }

    private void recordPriceChange(UUID productId, UUID variantId, BigDecimal oldPrice, BigDecimal newPrice, String reason) {
        PriceHistoryEntity history = new PriceHistoryEntity();
        history.setProductId(productId);
        history.setVariantId(variantId);
        history.setOldPrice(oldPrice);
        history.setNewPrice(newPrice);
        history.setReason(reason);
        history.setEffectiveAt(Instant.now());
        priceHistoryRepository.save(history);
    }

    private ProductDtos.Response toDetailedResponse(ProductEntity entity) {
        ProductDtos.Response response = productMapper.toResponse(entity);
        List<ProductDtos.VariantResponse> variants = variantRepository.findByProductId(entity.getId()).stream()
                .map(productMapper::toResponse).toList();
        List<ProductDtos.BarcodeResponse> barcodes = barcodeRepository.findByProductId(entity.getId()).stream()
                .map(productMapper::toResponse).toList();
        List<ProductDtos.ImageResponse> images = imageRepository.findByProductIdOrderBySortOrderAsc(entity.getId()).stream()
                .map(productMapper::toResponse).toList();
        return ProductDtos.Response.builder()
                .id(response.getId())
                .organizationId(response.getOrganizationId())
                .categoryId(response.getCategoryId())
                .brandId(response.getBrandId())
                .unitId(response.getUnitId())
                .sku(response.getSku())
                .name(response.getName())
                .description(response.getDescription())
                .basePrice(response.getBasePrice())
                .costPrice(response.getCostPrice())
                .taxRate(response.getTaxRate())
                .trackInventory(response.isTrackInventory())
                .trackExpiry(response.isTrackExpiry())
                .reorderLevel(response.getReorderLevel())
                .reorderQuantity(response.getReorderQuantity())
                .active(response.isActive())
                .variants(variants)
                .barcodes(barcodes)
                .images(images)
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
