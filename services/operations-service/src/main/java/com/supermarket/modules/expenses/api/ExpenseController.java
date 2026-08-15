package com.supermarket.modules.expenses.api;

import com.supermarket.modules.expenses.application.ExpenseService;
import com.supermarket.modules.expenses.domain.Expense;
import com.supermarket.modules.expenses.domain.ExpenseCategory;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/categories")
    public ApiResponse<List<ExpenseCategory>> listCategories() {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        return ApiResponse.success(expenseService.listCategories(orgId));
    }

    @PostMapping("/categories")
    public ApiResponse<ExpenseCategory> createCategory(@Valid @RequestBody ExpenseCategory category) {
        BranchContext.getOrganizationId().ifPresent(category::setOrganizationId);
        return ApiResponse.success(expenseService.createCategory(category));
    }

    @GetMapping
    public ApiResponse<PageResponse<Expense>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID branchId = BranchContext.getBranchId()
                .orElseThrow(() -> new IllegalArgumentException("X-Branch-Id header required"));
        return ApiResponse.success(PageResponse.from(expenseService.listByBranch(branchId, pageable)));
    }

    @PostMapping
    public ApiResponse<Expense> create(@Valid @RequestBody Expense expense) {
        return ApiResponse.success(expenseService.create(expense));
    }

    @GetMapping("/{id}")
    public ApiResponse<Expense> get(@PathVariable UUID id) {
        return ApiResponse.success(expenseService.getById(id));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Expense> approve(@PathVariable UUID id) {
        return ApiResponse.success(expenseService.approve(id));
    }
}
