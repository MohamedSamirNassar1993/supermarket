package com.supermarket.modules.suppliers.infrastructure;

import com.supermarket.modules.suppliers.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Optional<Supplier> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Supplier> findByOrganizationIdAndCode(UUID organizationId, String code);

    Page<Supplier> findByOrganizationId(UUID organizationId, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE s.organizationId = :orgId AND s.active = true")
    Page<Supplier> findActiveByOrganization(@Param("orgId") UUID organizationId, Pageable pageable);
}
