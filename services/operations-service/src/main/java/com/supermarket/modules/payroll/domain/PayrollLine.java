package com.supermarket.modules.payroll.domain;

import com.supermarket.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payroll_lines")
@Getter
@Setter
public class PayrollLine extends BaseEntity {

    @Column(name = "payroll_run_id", nullable = false)
    private UUID payrollRunId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "base_pay", nullable = false, precision = 19, scale = 4)
    private BigDecimal basePay = BigDecimal.ZERO;

    @Column(name = "overtime_pay", nullable = false, precision = 19, scale = 4)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "bonuses", nullable = false, precision = 19, scale = 4)
    private BigDecimal bonuses = BigDecimal.ZERO;

    @Column(name = "deductions", nullable = false, precision = 19, scale = 4)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "gross_pay", nullable = false, precision = 19, scale = 4)
    private BigDecimal grossPay = BigDecimal.ZERO;

    @Column(name = "net_pay", nullable = false, precision = 19, scale = 4)
    private BigDecimal netPay = BigDecimal.ZERO;
}
