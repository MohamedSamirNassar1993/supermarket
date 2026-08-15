package com.supermarket.modules.products.application.service;

import com.supermarket.modules.products.application.dto.ProductDtos;
import com.supermarket.modules.products.infrastructure.persistence.CategoryEntity;
import com.supermarket.modules.products.infrastructure.persistence.CategoryRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductRepository;
import com.supermarket.modules.products.infrastructure.persistence.ProductVariantEntity;
import com.supermarket.modules.products.infrastructure.persistence.ProductVariantRepository;
import com.supermarket.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DynamicPricingService {

    private static final int PRICE_SCALE = 4;

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ProductDtos.DynamicPriceResponse calculate(ProductDtos.DynamicPriceRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        BigDecimal costPrice = product.getCostPrice();
        BigDecimal markupPercent = resolveMarkupPercent(product, request);

        if (request.getVariantId() != null) {
            ProductVariantEntity variant = variantRepository.findByIdAndProductId(request.getVariantId(), product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product variant", request.getVariantId()));
            if (variant.getCostPrice() != null) {
                costPrice = variant.getCostPrice();
            }
            if (variant.getPrice() != null && request.getMarkupOverridePercent() == null) {
                return buildResponse(product.getId(), variant.getId(), costPrice, markupPercent, variant.getPrice(), product.getTaxRate());
            }
        }

        BigDecimal calculatedPrice = costPrice.multiply(BigDecimal.ONE.add(markupPercent.divide(BigDecimal.valueOf(100), PRICE_SCALE, RoundingMode.HALF_UP)))
                .setScale(PRICE_SCALE, RoundingMode.HALF_UP);

        return buildResponse(product.getId(), request.getVariantId(), costPrice, markupPercent, calculatedPrice, product.getTaxRate());
    }

    private BigDecimal resolveMarkupPercent(ProductEntity product, ProductDtos.DynamicPriceRequest request) {
        if (request.getMarkupOverridePercent() != null) {
            return request.getMarkupOverridePercent();
        }
        if (product.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            if (category != null && category.getMarkupPercent() != null) {
                return category.getMarkupPercent();
            }
        }
        if (product.getBasePrice().compareTo(BigDecimal.ZERO) > 0 && product.getCostPrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getBasePrice()
                    .subtract(product.getCostPrice())
                    .divide(product.getCostPrice(), PRICE_SCALE, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    private ProductDtos.DynamicPriceResponse buildResponse(UUID productId, UUID variantId, BigDecimal costPrice,
                                                           BigDecimal markupPercent, BigDecimal calculatedPrice, BigDecimal taxRate) {
        BigDecimal taxAmount = calculatedPrice.multiply(taxRate).divide(BigDecimal.valueOf(100), PRICE_SCALE, RoundingMode.HALF_UP);
        return ProductDtos.DynamicPriceResponse.builder()
                .productId(productId)
                .variantId(variantId)
                .costPrice(costPrice)
                .markupPercent(markupPercent)
                .calculatedPrice(calculatedPrice)
                .taxAmount(taxAmount)
                .priceWithTax(calculatedPrice.add(taxAmount))
                .build();
    }
}
