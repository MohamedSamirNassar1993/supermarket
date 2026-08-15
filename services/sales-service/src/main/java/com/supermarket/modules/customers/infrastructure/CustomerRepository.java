package com.supermarket.modules.customers.infrastructure;

import com.supermarket.modules.customers.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Customer> findByOrganizationIdAndCode(UUID organizationId, String code);

    Page<Customer> findByOrganizationId(UUID organizationId, Pageable pageable);
}
