package com.notification.services;

import com.notification.dtos.NotificationRequestDTO;
import com.notification.dtos.NotificationResponseDTO;
import com.notification.entities.DeviceToken;
import com.notification.entities.Notification;
import com.notification.repositories.DeviceTokenRepository;
import com.notification.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final DeviceTokenRepository deviceTokenRepository;
  private final FcmClient fcmClient;
  private final NotificationTemplateService templateService;
  private final MessageFormatter messageFormatter;

  public void sendNotification(NotificationRequestDTO request) {
    // Save to Database
    Notification notification = Notification.builder()
        .userId(request.getUserId())
        .title(request.getTitle())
        .message(request.getMessage())
        .isRead(false)
        .createdAt(LocalDateTime.now())
        .build();
    notificationRepository.save(notification);

    // Send via FCM if token exists
    deviceTokenRepository.findById(request.getUserId()).ifPresent(deviceToken -> {
      try {
        // Prepare data payload matching NotificationModel matches
        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(notification.getId()));
        data.put("title", notification.getTitle());
        data.put("message", notification.getMessage());
        // NotificationModel uses 'timestamp'
        data.put("timestamp", notification.getCreatedAt().toString());
        // For robustness, maybe add 'created_at' if the user insists, but strict model
        // match is 'timestamp'
        data.put("is_read", "false");

        fcmClient.sendMessage(deviceToken.getFcmToken(), request.getTitle(), request.getMessage(), data);
      } catch (Exception e) {
        log.error("Failed to send FCM message", e);
      }
    });
  }

  public void registerToken(Long userId, String token) {
    DeviceToken deviceToken = DeviceToken.builder()
        .userId(userId)
        .fcmToken(token)
        .build();
    deviceTokenRepository.save(deviceToken);
  }

  public List<NotificationResponseDTO> getNotifications(Long userId) {
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(n -> NotificationResponseDTO.builder()
            .id(n.getId())
            .title(n.getTitle())
            .message(n.getMessage())
            .timestamp(n.getCreatedAt())
            .isRead(n.getIsRead())
            .build())
        .collect(Collectors.toList());
  }
}
