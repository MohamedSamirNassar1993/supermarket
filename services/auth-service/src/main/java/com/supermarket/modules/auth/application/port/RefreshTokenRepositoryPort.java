package com.supermarket.modules.auth.application.port;

import com.supermarket.modules.auth.domain.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken refreshToken);

    void revokeAllByUserId(UUID userId);

    void revokeByTokenHash(String tokenHash);
}
