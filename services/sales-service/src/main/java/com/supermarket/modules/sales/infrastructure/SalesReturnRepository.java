package com.supermarket.modules.sales.infrastructure;

import com.supermarket.modules.sales.domain.SalesReturn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalesReturnRepository extends JpaRepository<SalesReturn, UUID> {

    Optional<SalesReturn> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
