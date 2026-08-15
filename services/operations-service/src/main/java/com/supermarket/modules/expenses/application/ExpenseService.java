package com.supermarket.modules.expenses.application;

import com.supermarket.modules.expenses.domain.Expense;
import com.supermarket.modules.expenses.domain.ExpenseCategory;
import com.supermarket.modules.expenses.infrastructure.persistence.ExpenseCategoryJpaRepository;
import com.supermarket.modules.expenses.infrastructure.persistence.ExpenseJpaRepository;
import com.supermarket.shared.audit.AuditAction;
import com.supermarket.shared.audit.Audited;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseCategoryJpaRepository categoryRepository;
    private final ExpenseJpaRepository expenseRepository;

    @Transactional(readOnly = true)
    public List<ExpenseCategory> listCategories(UUID organizationId) {
        return categoryRepository.findByOrganizationIdAndActiveTrue(organizationId);
    }

    @Transactional
    @Audited(entityType = "ExpenseCategory", action = AuditAction.CREATE)
    public ExpenseCategory createCategory(ExpenseCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Page<Expense> listByBranch(UUID branchId, Pageable pageable) {
        return expenseRepository.findByBranchId(branchId, pageable);
    }

    @Transactional
    @Audited(entityType = "Expense", action = AuditAction.CREATE)
    public Expense create(Expense expense) {
        BranchContext.getOrganizationId().ifPresent(expense::setOrganizationId);
        BranchContext.getBranchId().ifPresent(expense::setBranchId);
        return expenseRepository.save(expense);
    }

    @Transactional
    @Audited(entityType = "Expense", action = AuditAction.APPROVE)
    public Expense approve(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
        expense.setStatus("APPROVED");
        expense.setApprovedBy(BranchContext.getActorId());
        expense.setApprovedAt(Instant.now());
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Expense getById(UUID id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
    }
}
