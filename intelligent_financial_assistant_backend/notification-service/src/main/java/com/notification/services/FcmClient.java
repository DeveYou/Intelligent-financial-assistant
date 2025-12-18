package com.notification.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmClient {

  public void sendMessage(String token, String title, String body, Map<String, String> data) throws Exception {
    Notification notification = Notification.builder()
        .setTitle(title)
        .setBody(body)
        .build();

    com.google.firebase.messaging.AndroidConfig androidConfig = com.google.firebase.messaging.AndroidConfig.builder()
        .setNotification(com.google.firebase.messaging.AndroidNotification.builder()
            .setChannelId("bank_notifications")
            .build())
        .setPriority(com.google.firebase.messaging.AndroidConfig.Priority.HIGH)
        .build();

    Message.Builder messageBuilder = Message.builder()
        .setToken(token)
        .setNotification(notification)
        .setAndroidConfig(androidConfig);

    if (data != null) {
      messageBuilder.putAllData(data);
    }

    FirebaseMessaging.getInstance().send(messageBuilder.build());
  }
}
