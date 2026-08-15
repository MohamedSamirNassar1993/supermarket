package com.supermarket.modules.sales.infrastructure;

import com.supermarket.modules.sales.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Optional<Promotion> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Promotion> findByOrganizationIdAndCode(UUID organizationId, String code);

    @Query("""
            SELECT DISTINCT p FROM Promotion p LEFT JOIN FETCH p.rules
            WHERE p.organizationId = :orgId AND p.active = true
              AND p.startDate <= :now AND p.endDate >= :now
            ORDER BY p.priority DESC
            """)
    List<Promotion> findActivePromotions(@Param("orgId") UUID organizationId, @Param("now") Instant now);
}
