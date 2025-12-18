package com.notification.services;

import com.notification.entities.NotificationTemplate;
import com.notification.repositories.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {
  private final NotificationTemplateRepository repository;

  public NotificationTemplate getTemplate(String code) {
    return repository.findByCode(code)
        .orElse(NotificationTemplate.builder()
            .code(code)
            .titleTemplate("Notification")
            .messageTemplate("You have a new notification")
            .build());
  }
}
