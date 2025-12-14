package com.khaoula.recipientservice.service;

import com.khaoula.recipientservice.model.Recipient;

import java.util.List;
import java.util.Optional;

public interface RecipientService {
    List<Recipient> getRecipientList(Long userId);
    Recipient addRecipient(Recipient recipient, Long userId);
    Recipient updateRecipient(Long id, Recipient recipient, Long userId);
    void deleteRecipient(Long id, Long userId);
    Optional<Recipient> getByIban(String iban);
}
