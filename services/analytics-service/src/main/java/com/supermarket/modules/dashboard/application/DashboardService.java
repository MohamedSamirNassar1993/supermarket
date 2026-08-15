package com.supermarket.modules.dashboard.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.shared.multibranch.BranchContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DashboardService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final Optional<StringRedisTemplate> redisTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper,
                            @Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.redisTemplate = Optional.ofNullable(redisTemplate);
    }

    public Map<String, Object> getAggregates() {
        UUID orgId = BranchContext.getOrganizationId()
                .orElseThrow(() -> new IllegalArgumentException("X-Organization-Id header required"));
        UUID branchId = BranchContext.getBranchId().orElse(null);
        String cacheKey = "dashboard:" + orgId + ":" + (branchId != null ? branchId : "all");

        if (redisTemplate.isPresent()) {
            String cached = redisTemplate.get().opsForValue().get(cacheKey);
            if (cached != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> parsed = objectMapper.readValue(cached, Map.class);
                    return parsed;
                } catch (JsonProcessingException ignored) {
                    // fall through to recompute
                }
            }
        }

        Map<String, Object> aggregates = computeAggregates(orgId, branchId);
        redisTemplate.ifPresent(redis -> {
            try {
                redis.opsForValue().set(cacheKey, objectMapper.writeValueAsString(aggregates), CACHE_TTL);
            } catch (JsonProcessingException ignored) {
                // skip cache write
            }
        });
        return aggregates;
    }

    private Map<String, Object> computeAggregates(UUID orgId, UUID branchId) {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        BigDecimal todaySales = querySum(
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_invoices WHERE organization_id = ? AND (? IS NULL OR branch_id = ?) AND DATE(invoice_date) = ? AND status NOT IN ('DRAFT','CANCELLED')",
                orgId, branchId, branchId, today);

        BigDecimal monthSales = querySum(
                "SELECT COALESCE(SUM(total_amount), 0) FROM sales_invoices WHERE organization_id = ? AND (? IS NULL OR branch_id = ?) AND DATE(invoice_date) >= ? AND status NOT IN ('DRAFT','CANCELLED')",
                orgId, branchId, branchId, monthStart);

        BigDecimal monthExpenses = querySum(
                "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE organization_id = ? AND (? IS NULL OR branch_id = ?) AND expense_date >= ? AND status IN ('APPROVED','PAID')",
                orgId, branchId, branchId, monthStart);

        Long activeEmployees = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employees WHERE organization_id = ? AND (? IS NULL OR branch_id = ?) AND status = 'ACTIVE'",
                Long.class, orgId, branchId, branchId);

        result.put("organizationId", orgId);
        result.put("branchId", branchId);
        result.put("todaySales", todaySales);
        result.put("monthSales", monthSales);
        result.put("monthExpenses", monthExpenses);
        result.put("monthNetEstimate", monthSales.subtract(monthExpenses));
        result.put("activeEmployees", activeEmployees != null ? activeEmployees : 0L);
        result.put("asOf", today.toString());
        return result;
    }

    private BigDecimal querySum(String sql, Object... args) {
        BigDecimal value = jdbcTemplate.queryForObject(sql, BigDecimal.class, args);
        return value != null ? value : BigDecimal.ZERO;
    }
}
