package com.supermarket.modules.expenses.infrastructure.persistence;

import com.supermarket.modules.expenses.domain.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseCategoryJpaRepository extends JpaRepository<ExpenseCategory, UUID> {
    List<ExpenseCategory> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
