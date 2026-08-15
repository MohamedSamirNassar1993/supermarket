package com.supermarket.modules.notifications.infrastructure.persistence;

import com.supermarket.modules.notifications.domain.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceJpaRepository extends JpaRepository<NotificationPreference, UUID> {
    List<NotificationPreference> findByUserId(UUID userId);

    Optional<NotificationPreference> findByUserIdAndNotificationType(UUID userId, String notificationType);
}
