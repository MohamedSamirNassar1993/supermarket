package com.supermarket.shared.api;

import com.supermarket.shared.domain.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String ORG_HEADER = "X-Organization-Id";
    public static final String BRANCH_HEADER = "X-Branch-Id";
    public static final String ACTOR_HEADER = "X-Actor";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String orgHeader = request.getHeader(ORG_HEADER);
            if (orgHeader != null && !orgHeader.isBlank()) {
                TenantContext.setOrganizationId(UUID.fromString(orgHeader));
            }
            String branchHeader = request.getHeader(BRANCH_HEADER);
            if (branchHeader != null && !branchHeader.isBlank()) {
                TenantContext.setBranchId(UUID.fromString(branchHeader));
            }
            String actorHeader = request.getHeader(ACTOR_HEADER);
            if (actorHeader != null && !actorHeader.isBlank()) {
                TenantContext.setActor(actorHeader);
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
