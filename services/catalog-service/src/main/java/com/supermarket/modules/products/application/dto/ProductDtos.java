package com.supermarket.modules.products.application.dto;

import com.supermarket.modules.products.domain.BarcodeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ProductDtos {

    private ProductDtos() {
    }

    @Getter
    @Setter
    public static class CreateRequest {
        @NotNull
        private UUID organizationId;
        private UUID categoryId;
        private UUID brandId;
        @NotNull
        private UUID unitId;
        @NotBlank @Size(max = 100)
        private String sku;
        @NotBlank @Size(max = 255)
        private String name;
        private String description;
        @DecimalMin("0")
        private BigDecimal basePrice;
        @DecimalMin("0")
        private BigDecimal costPrice;
        @DecimalMin("0")
        private BigDecimal taxRate;
        private Boolean trackInventory;
        private Boolean trackExpiry;
        private BigDecimal reorderLevel;
        private BigDecimal reorderQuantity;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        private UUID categoryId;
        private UUID brandId;
        private UUID unitId;
        @Size(max = 255)
        private String name;
        private String description;
        @DecimalMin("0")
        private BigDecimal basePrice;
        @DecimalMin("0")
        private BigDecimal costPrice;
        @DecimalMin("0")
        private BigDecimal taxRate;
        private Boolean trackInventory;
        private Boolean trackExpiry;
        private BigDecimal reorderLevel;
        private BigDecimal reorderQuantity;
        private Boolean active;
    }

    @Getter
    @Setter
    public static class PriceUpdateRequest {
        @NotNull @DecimalMin("0")
        private BigDecimal newPrice;
        private String reason;
        private UUID variantId;
    }

    @Getter
    @Setter
    public static class VariantRequest {
        @NotBlank @Size(max = 100)
        private String sku;
        @NotBlank @Size(max = 255)
        private String name;
        private Map<String, String> attributes;
        @DecimalMin("0")
        private BigDecimal price;
        @DecimalMin("0")
        private BigDecimal costPrice;
        private Boolean active;
    }

    @Getter
    @Setter
    public static class BarcodeRequest {
        @NotBlank @Size(max = 100)
        private String barcode;
        private BarcodeType barcodeType;
        private UUID variantId;
        private Boolean primaryBarcode;
    }

    @Getter
    @Setter
    public static class ImageRequest {
        @NotBlank @Size(max = 2048)
        private String url;
        private UUID variantId;
        private Integer sortOrder;
        private Boolean primaryImage;
    }

    @Getter
    @Setter
    public static class DynamicPriceRequest {
        @NotNull
        private UUID productId;
        private UUID variantId;
        private BigDecimal markupOverridePercent;
    }

    @Getter
    @Builder
    public static class Response {
        private UUID id;
        private UUID organizationId;
        private UUID categoryId;
        private UUID brandId;
        private UUID unitId;
        private String sku;
        private String name;
        private String description;
        private BigDecimal basePrice;
        private BigDecimal costPrice;
        private BigDecimal taxRate;
        private boolean trackInventory;
        private boolean trackExpiry;
        private BigDecimal reorderLevel;
        private BigDecimal reorderQuantity;
        private boolean active;
        private List<VariantResponse> variants;
        private List<BarcodeResponse> barcodes;
        private List<ImageResponse> images;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Getter
    @Builder
    public static class VariantResponse {
        private UUID id;
        private UUID productId;
        private String sku;
        private String name;
        private Map<String, String> attributes;
        private BigDecimal price;
        private BigDecimal costPrice;
        private boolean active;
    }

    @Getter
    @Builder
    public static class BarcodeResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private String barcode;
        private BarcodeType barcodeType;
        private boolean primaryBarcode;
    }

    @Getter
    @Builder
    public static class ImageResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private String url;
        private int sortOrder;
        private boolean primaryImage;
    }

    @Getter
    @Builder
    public static class PriceHistoryResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private BigDecimal oldPrice;
        private BigDecimal newPrice;
        private String reason;
        private Instant effectiveAt;
        private String changedBy;
    }

    @Getter
    @Builder
    public static class DynamicPriceResponse {
        private UUID productId;
        private UUID variantId;
        private BigDecimal costPrice;
        private BigDecimal markupPercent;
        private BigDecimal calculatedPrice;
        private BigDecimal taxAmount;
        private BigDecimal priceWithTax;
    }
}
