package com.notification.services;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class MessageFormatter {

  public String format(String template, Map<String, String> variables) {
    if (template == null)
      return "";
    String result = template;
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      result = result.replace("{" + entry.getKey() + "}", entry.getValue());
    }
    return result;
  }
}
