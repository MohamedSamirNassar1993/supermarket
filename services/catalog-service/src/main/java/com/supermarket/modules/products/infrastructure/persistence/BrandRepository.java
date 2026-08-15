package com.supermarket.modules.products.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {

    Page<BrandEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Page<BrandEntity> findByOrganizationIdAndActive(UUID organizationId, boolean active, Pageable pageable);

    Optional<BrandEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);

    boolean existsByOrganizationIdAndCodeAndIdNot(UUID organizationId, String code, UUID id);
}
