package com.supermarket.modules.notifications.application;

import com.supermarket.modules.notifications.domain.Notification;
import com.supermarket.modules.notifications.domain.NotificationPreference;
import com.supermarket.modules.notifications.infrastructure.persistence.NotificationJpaRepository;
import com.supermarket.modules.notifications.infrastructure.persistence.NotificationPreferenceJpaRepository;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationJpaRepository notificationRepository;
    private final NotificationPreferenceJpaRepository preferenceRepository;

    public NotificationService(NotificationJpaRepository notificationRepository,
                               NotificationPreferenceJpaRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public Notification create(Notification notification) {
        BranchContext.getOrganizationId().ifPresent(notification::setOrganizationId);
        BranchContext.getBranchId().ifPresent(notification::setBranchId);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification notifyUser(UUID userId, String type, String title, String message, String severity) {
        if (!isInAppEnabled(userId, type)) {
            log.debug("In-app notification disabled for user {} type {}", userId, type);
            return null;
        }
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSeverity(severity != null ? severity : "INFO");
        BranchContext.getOrganizationId().ifPresent(notification::setOrganizationId);
        BranchContext.getBranchId().ifPresent(notification::setBranchId);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<Notification> listUnread(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public Notification markRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found: " + id));
        notification.setReadAt(Instant.now());
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndReadAtIsNull(userId);
    }

    @Transactional
    public NotificationPreference upsertPreference(NotificationPreference preference) {
        return preferenceRepository.save(preference);
    }

    @Transactional(readOnly = true)
    public List<NotificationPreference> listPreferences(UUID userId) {
        return preferenceRepository.findByUserId(userId);
    }

    private boolean isInAppEnabled(UUID userId, String type) {
        return preferenceRepository.findByUserIdAndNotificationType(userId, type)
                .map(NotificationPreference::isInAppEnabled)
                .orElse(true);
    }
}
