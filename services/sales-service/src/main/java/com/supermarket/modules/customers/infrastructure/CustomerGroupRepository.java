package com.supermarket.modules.customers.infrastructure;

import com.supermarket.modules.customers.domain.CustomerGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, UUID> {

    Optional<CustomerGroup> findByIdAndOrganizationId(UUID id, UUID organizationId);

    List<CustomerGroup> findByOrganizationId(UUID organizationId);
}
