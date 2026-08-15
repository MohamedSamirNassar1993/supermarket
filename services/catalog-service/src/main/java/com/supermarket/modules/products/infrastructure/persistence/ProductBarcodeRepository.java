package com.supermarket.modules.products.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductBarcodeRepository extends JpaRepository<ProductBarcodeEntity, UUID> {

    List<ProductBarcodeEntity> findByProductId(UUID productId);

    Optional<ProductBarcodeEntity> findByBarcode(String barcode);

    Optional<ProductBarcodeEntity> findByIdAndProductId(UUID id, UUID productId);

    boolean existsByBarcode(String barcode);
}
