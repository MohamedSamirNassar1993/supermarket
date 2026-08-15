package com.supermarket.modules.notifications.api;

import com.supermarket.modules.notifications.application.NotificationService;
import com.supermarket.modules.notifications.domain.Notification;
import com.supermarket.modules.notifications.domain.NotificationPreference;
import com.supermarket.shared.api.ApiResponse;
import com.supermarket.shared.api.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/users/{userId}/unread")
    public ApiResponse<PageResponse<Notification>> listUnread(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(notificationService.listUnread(userId, pageable)));
    }

    @GetMapping("/users/{userId}/unread/count")
    public ApiResponse<Long> countUnread(@PathVariable UUID userId) {
        return ApiResponse.success(notificationService.countUnread(userId));
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Notification> markRead(@PathVariable UUID id) {
        return ApiResponse.success(notificationService.markRead(id));
    }

    @PostMapping
    public ApiResponse<Notification> create(@Valid @RequestBody Notification notification) {
        return ApiResponse.success(notificationService.create(notification));
    }

    @GetMapping("/users/{userId}/preferences")
    public ApiResponse<List<NotificationPreference>> listPreferences(@PathVariable UUID userId) {
        return ApiResponse.success(notificationService.listPreferences(userId));
    }

    @PutMapping("/preferences")
    public ApiResponse<NotificationPreference> upsertPreference(
            @Valid @RequestBody NotificationPreference preference) {
        return ApiResponse.success(notificationService.upsertPreference(preference));
    }
}
