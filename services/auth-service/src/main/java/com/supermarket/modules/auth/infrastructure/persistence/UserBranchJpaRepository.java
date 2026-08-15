package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.domain.UserBranch;
import com.supermarket.modules.auth.domain.UserBranchId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserBranchJpaRepository extends JpaRepository<UserBranch, UserBranchId> {

    List<UserBranch> findByIdUserId(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserBranch ub WHERE ub.id.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
