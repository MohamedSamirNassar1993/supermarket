package com.supermarket.modules.platform.application;

import com.supermarket.modules.platform.domain.AuditLog;
import com.supermarket.modules.platform.infrastructure.persistence.AuditLogJpaRepository;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "passwordHash", "token", "secret", "cardNumber", "cvv"
    );

    private final AuditLogJpaRepository auditLogRepository;

    @Transactional
    public AuditLog record(String entityType, UUID entityId, String action, Map<String, Object> changes) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setChanges(redactSensitive(changes));
        log.setActorId(BranchContext.getActorId());
        log.setActorName(BranchContext.getActorName());
        log.setOccurredAt(Instant.now());
        BranchContext.getOrganizationId().ifPresent(log::setOrganizationId);
        BranchContext.getBranchId().ifPresent(log::setBranchId);
        captureRequestMetadata(log);
        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> listByOrganization(UUID organizationId, Pageable pageable) {
        return auditLogRepository.findByOrganizationIdOrderByOccurredAtDesc(organizationId, pageable);
    }

    private void captureRequestMetadata(AuditLog log) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }
    }

    private Map<String, Object> redactSensitive(Map<String, Object> changes) {
        if (changes == null) {
            return null;
        }
        return changes.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> SENSITIVE_FIELDS.contains(entry.getKey()) ? "[REDACTED]" : entry.getValue()
                ));
    }
}
