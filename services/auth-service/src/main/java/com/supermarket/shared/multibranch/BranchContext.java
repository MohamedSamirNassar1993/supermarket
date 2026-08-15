package com.supermarket.shared.multibranch;

import java.util.Optional;
import java.util.UUID;

public final class BranchContext {

    private static final ThreadLocal<UUID> ORGANIZATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<UUID> BRANCH_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ACTOR_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ACTOR_NAME = new ThreadLocal<>();

    private BranchContext() {
    }

    public static void setOrganizationId(UUID organizationId) {
        ORGANIZATION_ID.set(organizationId);
    }

    public static Optional<UUID> getOrganizationId() {
        return Optional.ofNullable(ORGANIZATION_ID.get());
    }

    public static void setBranchId(UUID branchId) {
        BRANCH_ID.set(branchId);
    }

    public static Optional<UUID> getBranchId() {
        return Optional.ofNullable(BRANCH_ID.get());
    }

    public static void setActor(String actorId, String actorName) {
        ACTOR_ID.set(actorId);
        ACTOR_NAME.set(actorName);
    }

    public static String getActorId() {
        return ACTOR_ID.get() != null ? ACTOR_ID.get() : "SYSTEM";
    }

    public static String getActorName() {
        return ACTOR_NAME.get() != null ? ACTOR_NAME.get() : "System";
    }

    public static void clear() {
        ORGANIZATION_ID.remove();
        BRANCH_ID.remove();
        ACTOR_ID.remove();
        ACTOR_NAME.remove();
    }
}
