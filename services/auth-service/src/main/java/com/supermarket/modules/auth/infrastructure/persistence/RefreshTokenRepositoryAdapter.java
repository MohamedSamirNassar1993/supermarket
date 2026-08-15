package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.application.port.RefreshTokenRepositoryPort;
import com.supermarket.modules.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        refreshTokenJpaRepository.revokeAllByUserId(userId, Instant.now());
    }

    @Override
    @Transactional
    public void revokeByTokenHash(String tokenHash) {
        refreshTokenJpaRepository.revokeByTokenHash(tokenHash, Instant.now());
    }
}
