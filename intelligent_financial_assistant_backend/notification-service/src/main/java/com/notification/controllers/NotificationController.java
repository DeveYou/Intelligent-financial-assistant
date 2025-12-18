package com.notification.controllers;

import com.notification.dtos.NotificationRequestDTO;
import com.notification.dtos.NotificationResponseDTO;
import com.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/send")
  public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequestDTO request) {
    notificationService.sendNotification(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/save-token")
  public ResponseEntity<Void> saveToken(@RequestParam String token, @RequestParam(name = "userId") Long userId) {
    notificationService.registerToken(userId, token);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<NotificationResponseDTO>> getNotifications(@RequestParam(name = "userId") Long userId) {
    return ResponseEntity.ok(notificationService.getNotifications(userId));
  }
}
