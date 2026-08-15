package com.supermarket.modules.payroll.application;

import com.supermarket.modules.hr.domain.Employee;
import com.supermarket.modules.hr.infrastructure.persistence.EmployeeJpaRepository;
import com.supermarket.modules.payroll.domain.PayrollLine;
import com.supermarket.modules.payroll.domain.PayrollRun;
import com.supermarket.modules.payroll.domain.SalaryAdvance;
import com.supermarket.modules.payroll.domain.EmployeeLoan;
import com.supermarket.modules.payroll.infrastructure.persistence.EmployeeLoanJpaRepository;
import com.supermarket.modules.payroll.infrastructure.persistence.PayrollLineJpaRepository;
import com.supermarket.modules.payroll.infrastructure.persistence.PayrollRunJpaRepository;
import com.supermarket.modules.payroll.infrastructure.persistence.SalaryAdvanceJpaRepository;
import com.supermarket.shared.audit.AuditAction;
import com.supermarket.shared.audit.Audited;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRunJpaRepository payrollRunRepository;
    private final PayrollLineJpaRepository payrollLineRepository;
    private final EmployeeJpaRepository employeeRepository;
    private final SalaryAdvanceJpaRepository salaryAdvanceRepository;
    private final EmployeeLoanJpaRepository employeeLoanRepository;

    @Transactional(readOnly = true)
    public List<PayrollRun> listRuns(UUID organizationId) {
        return payrollRunRepository.findByOrganizationIdOrderByPeriodStartDesc(organizationId);
    }

    @Transactional
    @Audited(entityType = "PayrollRun", action = AuditAction.CREATE)
    public PayrollRun createRun(PayrollRun run) {
        BranchContext.getOrganizationId().ifPresent(run::setOrganizationId);
        BranchContext.getBranchId().ifPresent(run::setBranchId);
        return payrollRunRepository.save(run);
    }

    @Transactional
    @Audited(entityType = "PayrollRun", action = AuditAction.STATUS_CHANGE)
    public PayrollRun processRun(UUID runId) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new EntityNotFoundException("Payroll run not found: " + runId));
        UUID branchId = run.getBranchId();
        List<Employee> employees = branchId != null
                ? employeeRepository.findByBranchIdAndStatus(branchId, "ACTIVE")
                : List.of();

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        for (Employee employee : employees) {
            PayrollLine line = new PayrollLine();
            line.setPayrollRunId(runId);
            line.setEmployeeId(employee.getId());
            line.setBasePay(employee.getBaseSalary());
            line.setGrossPay(employee.getBaseSalary());
            line.setNetPay(employee.getBaseSalary());
            payrollLineRepository.save(line);
            totalGross = totalGross.add(line.getGrossPay());
            totalNet = totalNet.add(line.getNetPay());
        }

        run.setTotalGross(totalGross);
        run.setTotalDeductions(totalDeductions);
        run.setTotalNet(totalNet);
        run.setStatus("COMPLETED");
        run.setProcessedAt(Instant.now());
        return payrollRunRepository.save(run);
    }

    @Transactional(readOnly = true)
    public List<PayrollLine> getLines(UUID runId) {
        return payrollLineRepository.findByPayrollRunId(runId);
    }

    @Transactional
    @Audited(entityType = "SalaryAdvance", action = AuditAction.CREATE)
    public SalaryAdvance createAdvance(SalaryAdvance advance) {
        advance.setRemainingBalance(advance.getAmount());
        if (advance.getRepaymentMonths() > 0) {
            advance.setMonthlyDeduction(advance.getAmount()
                    .divide(BigDecimal.valueOf(advance.getRepaymentMonths()), 4, java.math.RoundingMode.HALF_UP));
        }
        return salaryAdvanceRepository.save(advance);
    }

    @Transactional
    @Audited(entityType = "EmployeeLoan", action = AuditAction.CREATE)
    public EmployeeLoan createLoan(EmployeeLoan loan) {
        loan.setRemainingBalance(loan.getPrincipal());
        return employeeLoanRepository.save(loan);
    }
}
