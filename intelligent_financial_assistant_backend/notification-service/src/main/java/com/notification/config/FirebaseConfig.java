package com.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

  @org.springframework.beans.factory.annotation.Value("${FIREBASE_CONFIG_PATH}")
  private String firebaseConfigPath;

  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    GoogleCredentials credentials;

    if (firebaseConfigPath != null && !firebaseConfigPath.isEmpty()) {
      try (FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
        credentials = GoogleCredentials.fromStream(serviceAccount);
      }
    } else {
      // Fallback or default behavior
      credentials = GoogleCredentials.getApplicationDefault();
    }

    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build();

    if (FirebaseApp.getApps().isEmpty()) {
      return FirebaseApp.initializeApp(options);
    } else {
      return FirebaseApp.getInstance();
    }
  }
}
