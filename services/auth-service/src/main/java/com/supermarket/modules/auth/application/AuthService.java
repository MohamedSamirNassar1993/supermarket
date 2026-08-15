package com.supermarket.modules.auth.application;

import com.supermarket.modules.auth.api.dto.AuthResponse;
import com.supermarket.modules.auth.api.dto.LoginRequest;
import com.supermarket.modules.auth.api.dto.LogoutRequest;
import com.supermarket.modules.auth.api.dto.RefreshTokenRequest;
import com.supermarket.modules.auth.api.mapper.AuthMapper;
import com.supermarket.modules.auth.application.port.RefreshTokenRepositoryPort;
import com.supermarket.modules.auth.application.port.TwoFactorPort;
import com.supermarket.modules.auth.application.port.UserRepositoryPort;
import com.supermarket.modules.auth.domain.RefreshToken;
import com.supermarket.modules.auth.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginHistoryService loginHistoryService;
    private final TwoFactorPort twoFactorPort;
    private final AuthMapper authMapper;
    private final AuthEventPublisher authEventPublisher;

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepositoryPort.findByEmailOrUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            loginHistoryService.recordFailure(request.getUsername(), "Invalid credentials", ipAddress, userAgent);
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isActive()) {
            loginHistoryService.recordFailure(request.getUsername(), "Account inactive", ipAddress, userAgent);
            throw new BadCredentialsException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginHistoryService.recordFailure(request.getUsername(), "Invalid credentials", ipAddress, userAgent);
            throw new BadCredentialsException("Invalid credentials");
        }

        if (twoFactorPort.isEnabled(user)) {
            if (request.getTwoFactorCode() == null || request.getTwoFactorCode().isBlank()) {
                twoFactorPort.sendChallenge(user);
                loginHistoryService.recordFailure(request.getUsername(), "Two-factor code required", ipAddress, userAgent);
                throw new BadCredentialsException("Two-factor authentication code required");
            }
            if (!twoFactorPort.verify(user, request.getTwoFactorCode())) {
                loginHistoryService.recordFailure(request.getUsername(), "Invalid two-factor code", ipAddress, userAgent);
                throw new BadCredentialsException("Invalid two-factor authentication code");
            }
        }

        user.setLastLoginAt(Instant.now());
        userRepositoryPort.save(user);

        loginHistoryService.recordSuccess(user.getId(), user.getEmail(), ipAddress, userAgent);
        authEventPublisher.publishLoginSuccess(user.getId(), user.getEmail());

        return buildAuthResponse(user, ipAddress, userAgent);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request, String ipAddress, String userAgent) {
        String rawRefreshToken = request.getRefreshToken();
        Claims claims;
        try {
            claims = jwtService.parseToken(rawRefreshToken);
        } catch (JwtException ex) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (!jwtService.isRefreshToken(claims)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken storedToken = refreshTokenRepositoryPort.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        UUID userId = jwtService.extractUserId(claims);
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!user.isActive()) {
            throw new BadCredentialsException("Account is inactive");
        }

        refreshTokenRepositoryPort.revokeByTokenHash(tokenHash);
        return buildAuthResponse(user, ipAddress, userAgent);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return;
        }
        try {
            Claims claims = jwtService.parseToken(request.getRefreshToken());
            if (jwtService.isRefreshToken(claims)) {
                refreshTokenRepositoryPort.revokeByTokenHash(hashToken(request.getRefreshToken()));
            }
        } catch (JwtException ignored) {
            // Ignore invalid tokens on logout
        }
    }

    private AuthResponse buildAuthResponse(User user, String ipAddress, String userAgent) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(user.getId(), refreshToken, ipAddress, userAgent);
        return authMapper.toAuthResponse(user, accessToken, refreshToken, jwtService.getAccessTokenExpirationMs());
    }

    private void persistRefreshToken(UUID userId, String rawToken, String ipAddress, String userAgent) {
        RefreshToken entity = new RefreshToken();
        entity.setUserId(userId);
        entity.setTokenHash(hashToken(rawToken));
        entity.setExpiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpirationMs()));
        entity.setRevoked(false);
        entity.setIpAddress(ipAddress);
        entity.setUserAgent(userAgent);
        refreshTokenRepositoryPort.save(entity);
    }

    static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
