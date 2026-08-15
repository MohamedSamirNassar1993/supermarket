package com.supermarket.shared.audit;

import com.supermarket.modules.platform.application.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result")
    public void afterAuditedMethod(JoinPoint joinPoint, Audited audited, Object result) {
        UUID entityId = extractEntityId(result, joinPoint.getArgs());
        Map<String, Object> changes = new HashMap<>();
        changes.put("method", joinPoint.getSignature().getName());
        if (result != null) {
            changes.put("after", result.toString());
        }
        auditService.record(audited.entityType(), entityId, audited.action().name(), changes);
    }

    private UUID extractEntityId(Object result, Object[] args) {
        if (result != null) {
            UUID fromResult = tryGetId(result);
            if (fromResult != null) {
                return fromResult;
            }
        }
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
            UUID fromArg = tryGetId(arg);
            if (fromArg != null) {
                return fromArg;
            }
        }
        return null;
    }

    private UUID tryGetId(Object target) {
        if (target == null) {
            return null;
        }
        try {
            Method getId = target.getClass().getMethod("getId");
            Object id = getId.invoke(target);
            if (id instanceof UUID uuid) {
                return uuid;
            }
        } catch (ReflectiveOperationException ignored) {
            // entity may not expose getId
        }
        return null;
    }
}
