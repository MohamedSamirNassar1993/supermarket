package com.supermarket.modules.payroll.domain;

import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "salary_advances")
@Getter
@Setter
public class SalaryAdvance extends AuditableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "advance_number", nullable = false, length = 50)
    private String advanceNumber;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "advance_date", nullable = false)
    private LocalDate advanceDate;

    @Column(name = "repayment_months", nullable = false)
    private int repaymentMonths = 1;

    @Column(name = "monthly_deduction", nullable = false, precision = 19, scale = 4)
    private BigDecimal monthlyDeduction;

    @Column(name = "remaining_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal remainingBalance;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
