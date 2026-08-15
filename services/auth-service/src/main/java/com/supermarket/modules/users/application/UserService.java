package com.supermarket.modules.users.application;

import com.supermarket.modules.auth.application.AuthEventPublisher;
import com.supermarket.modules.auth.application.port.RefreshTokenRepositoryPort;
import com.supermarket.modules.auth.application.port.RoleRepositoryPort;
import com.supermarket.modules.auth.application.port.UserBranchRepositoryPort;
import com.supermarket.modules.auth.application.port.UserRepositoryPort;
import com.supermarket.modules.auth.domain.Role;
import com.supermarket.modules.auth.domain.User;
import com.supermarket.modules.auth.domain.UserBranch;
import com.supermarket.modules.auth.domain.UserBranchId;
import com.supermarket.modules.users.api.dto.CreateUserRequest;
import com.supermarket.modules.users.api.dto.ResetPasswordRequest;
import com.supermarket.modules.users.api.dto.UpdateUserRequest;
import com.supermarket.modules.users.api.dto.UserResponse;
import com.supermarket.modules.users.api.mapper.UserMapper;
import com.supermarket.shared.api.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final UserBranchRepositoryPort userBranchRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthEventPublisher authEventPublisher;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepositoryPort.findAll(pageable)
                .map(user -> userMapper.toResponse(user, userBranchRepositoryPort.findByUserId(user.getId())));
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        return userMapper.toResponse(user, userBranchRepositoryPort.findByUserId(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepositoryPort.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use: " + request.getEmail());
        }
        if (userRepositoryPort.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already in use: " + request.getUsername());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setOrganizationId(request.getOrganizationId());
        user.setActive(true);
        user.setEmailVerified(false);
        user.setTwoFactorEnabled(false);
        user.setPasswordChangedAt(Instant.now());

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            user.setRoles(new HashSet<>(roleRepositoryPort.findAllById(request.getRoleIds())));
        }

        User saved = userRepositoryPort.save(user);
        syncUserBranches(saved.getId(), request.getBranchIds(), request.getPrimaryBranchId());
        authEventPublisher.publishUserCreated(saved.getId(), saved.getEmail(),
                saved.getFirstName() + " " + saved.getLastName());

        return userMapper.toResponse(saved, userBranchRepositoryPort.findByUserId(saved.getId()));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepositoryPort.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new ConflictException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepositoryPort.existsByUsernameAndIdNot(request.getUsername(), id)) {
                throw new ConflictException("Username already in use: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getOrganizationId() != null) {
            user.setOrganizationId(request.getOrganizationId());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
            if (!request.getActive()) {
                refreshTokenRepositoryPort.revokeAllByUserId(id);
            }
        }
        if (request.getRoleIds() != null) {
            user.setRoles(new HashSet<>(roleRepositoryPort.findAllById(request.getRoleIds())));
        }

        User saved = userRepositoryPort.save(user);

        if (request.getBranchIds() != null) {
            syncUserBranches(id, request.getBranchIds(), request.getPrimaryBranchId());
        }

        return userMapper.toResponse(saved, userBranchRepositoryPort.findByUserId(id));
    }

    @Transactional
    public void resetPassword(UUID id, ResetPasswordRequest request) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepositoryPort.save(user);
        refreshTokenRepositoryPort.revokeAllByUserId(id);
    }

    @Transactional
    public UserResponse deactivate(UUID id) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        user.setActive(false);
        User saved = userRepositoryPort.save(user);
        refreshTokenRepositoryPort.revokeAllByUserId(id);

        return userMapper.toResponse(saved, userBranchRepositoryPort.findByUserId(id));
    }

    private void syncUserBranches(UUID userId, Set<UUID> branchIds, UUID primaryBranchId) {
        userBranchRepositoryPort.deleteByUserId(userId);

        if (branchIds == null || branchIds.isEmpty()) {
            return;
        }

        List<UserBranch> assignments = new ArrayList<>();
        for (UUID branchId : branchIds) {
            UserBranch userBranch = new UserBranch();
            userBranch.setId(new UserBranchId(userId, branchId));
            userBranch.setPrimaryBranch(primaryBranchId != null && primaryBranchId.equals(branchId));
            userBranch.setCreatedAt(Instant.now());
            assignments.add(userBranch);
        }

        userBranchRepositoryPort.saveAll(assignments);
    }
}
