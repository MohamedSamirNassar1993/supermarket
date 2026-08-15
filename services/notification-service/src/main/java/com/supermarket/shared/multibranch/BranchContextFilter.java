package com.supermarket.shared.multibranch;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class BranchContextFilter extends OncePerRequestFilter {

    public static final String ORGANIZATION_HEADER = "X-Organization-Id";
    public static final String BRANCH_HEADER = "X-Branch-Id";
    public static final String ACTOR_ID_HEADER = "X-Actor-Id";
    public static final String ACTOR_NAME_HEADER = "X-Actor-Name";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            parseUuidHeader(request, ORGANIZATION_HEADER).ifPresent(BranchContext::setOrganizationId);
            parseUuidHeader(request, BRANCH_HEADER).ifPresent(BranchContext::setBranchId);
            String actorId = request.getHeader(ACTOR_ID_HEADER);
            String actorName = request.getHeader(ACTOR_NAME_HEADER);
            if (actorId != null || actorName != null) {
                BranchContext.setActor(actorId, actorName);
            }
            filterChain.doFilter(request, response);
        } finally {
            BranchContext.clear();
        }
    }

    private java.util.Optional<UUID> parseUuidHeader(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (value == null || value.isBlank()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException ex) {
            return java.util.Optional.empty();
        }
    }
}
