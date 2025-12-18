package com.notification.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
  private Long id;
  private String title;
  private String message;
  private LocalDateTime timestamp;
  @JsonProperty("is_read")
  private boolean isRead;
}
