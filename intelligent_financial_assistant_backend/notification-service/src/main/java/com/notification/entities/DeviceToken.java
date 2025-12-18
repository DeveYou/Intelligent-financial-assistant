package com.notification.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "device_tokens")
public class DeviceToken {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "fcm_token", nullable = false)
  private String fcmToken;
}
