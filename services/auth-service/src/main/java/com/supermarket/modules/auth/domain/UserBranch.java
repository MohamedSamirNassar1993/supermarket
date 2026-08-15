package com.supermarket.modules.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_branches")
public class UserBranch {

    @EmbeddedId
    private UserBranchId id;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryBranch;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
