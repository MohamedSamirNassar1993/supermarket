package com.supermarket.modules.purchases.infrastructure;

import com.supermarket.modules.purchases.domain.SupplierPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, UUID> {
}
