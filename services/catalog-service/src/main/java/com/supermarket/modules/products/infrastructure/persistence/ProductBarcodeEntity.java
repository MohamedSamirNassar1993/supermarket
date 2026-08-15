package com.supermarket.modules.products.infrastructure.persistence;

import com.supermarket.modules.products.domain.BarcodeType;
import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "product_barcodes")
@Getter
@Setter
public class ProductBarcodeEntity extends AuditableEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(nullable = false, length = 100, unique = true)
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "barcode_type", nullable = false, length = 20)
    private BarcodeType barcodeType = BarcodeType.EAN13;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryBarcode;
}
