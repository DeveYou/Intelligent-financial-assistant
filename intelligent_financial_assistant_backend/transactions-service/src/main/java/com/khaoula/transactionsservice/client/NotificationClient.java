package com.khaoula.transactionsservice.client;

import com.khaoula.transactionsservice.dto.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {

  @PostMapping("/api/v1/notifications/send")
  void sendNotification(@RequestBody NotificationRequestDTO request);
}
