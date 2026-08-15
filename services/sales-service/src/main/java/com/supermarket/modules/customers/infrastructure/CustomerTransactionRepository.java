package com.supermarket.modules.customers.infrastructure;

import com.supermarket.modules.customers.domain.CustomerTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, UUID> {

    Page<CustomerTransaction> findByCustomerIdOrderByTransactionDateDescCreatedAtDesc(
            UUID customerId, Pageable pageable);
}
