package com.supermarket.modules.auth.application.port;

import com.supermarket.modules.auth.domain.UserBranch;

import java.util.List;
import java.util.UUID;

public interface UserBranchRepositoryPort {

    List<UserBranch> findByUserId(UUID userId);

    void saveAll(List<UserBranch> branches);

    void deleteByUserId(UUID userId);
}
