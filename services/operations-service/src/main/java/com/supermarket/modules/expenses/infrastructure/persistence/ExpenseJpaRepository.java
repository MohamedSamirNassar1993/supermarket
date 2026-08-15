package com.supermarket.modules.expenses.infrastructure.persistence;

import com.supermarket.modules.expenses.domain.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByBranchId(UUID branchId, Pageable pageable);
}
