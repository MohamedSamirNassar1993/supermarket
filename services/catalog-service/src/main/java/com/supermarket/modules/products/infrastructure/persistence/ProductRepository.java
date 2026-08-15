package com.supermarket.modules.products.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

    Page<ProductEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<ProductEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndSku(UUID organizationId, String sku);

    boolean existsByOrganizationIdAndSkuAndIdNot(UUID organizationId, String sku, UUID id);
}
