package com.supermarket.shared.domain;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> ORGANIZATION = new ThreadLocal<>();
    private static final ThreadLocal<UUID> BRANCH = new ThreadLocal<>();
    private static final ThreadLocal<String> ACTOR = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setOrganizationId(UUID organizationId) {
        ORGANIZATION.set(organizationId);
    }

    public static UUID getOrganizationId() {
        return ORGANIZATION.get();
    }

    public static void setBranchId(UUID branchId) {
        BRANCH.set(branchId);
    }

    public static UUID getBranchId() {
        return BRANCH.get();
    }

    public static void setActor(String actor) {
        ACTOR.set(actor);
    }

    public static String getActor() {
        String actor = ACTOR.get();
        return actor != null ? actor : "SYSTEM";
    }

    public static void clear() {
        ORGANIZATION.remove();
        BRANCH.remove();
        ACTOR.remove();
    }
}
