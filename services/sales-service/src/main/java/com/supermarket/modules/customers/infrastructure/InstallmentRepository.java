package com.supermarket.modules.customers.infrastructure;

import com.supermarket.modules.customers.domain.Installment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {

    List<Installment> findByCustomerIdAndReferenceTypeAndReferenceId(
            UUID customerId, String referenceType, UUID referenceId);
}
