package com.notification.dtos;

import lombok.Data;

@Data
public class NotificationRequestDTO {
  private Long userId;
  private String title;
  private String message;
}
