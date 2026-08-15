package com.supermarket.modules.suppliers.infrastructure;

import com.supermarket.modules.suppliers.domain.SupplierTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface SupplierTransactionRepository extends JpaRepository<SupplierTransaction, UUID> {

    Page<SupplierTransaction> findBySupplierIdOrderByTransactionDateDescCreatedAtDesc(UUID supplierId, Pageable pageable);

    @Query(value = """
            SELECT balance_after FROM supplier_transactions
            WHERE supplier_id = :supplierId
            ORDER BY transaction_date DESC, created_at DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BigDecimal> findLatestBalance(@Param("supplierId") UUID supplierId);
}
