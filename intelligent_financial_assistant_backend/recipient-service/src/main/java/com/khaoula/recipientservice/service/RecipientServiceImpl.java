package com.khaoula.recipientservice.service;

import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.repository.RecipientRepository;
import com.khaoula.recipientservice.exception.ResourceNotFoundException;
import com.khaoula.recipientservice.exception.ForbiddenException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipientServiceImpl implements RecipientService {

    private final RecipientRepository recipientRepository;

    public RecipientServiceImpl(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    @Override
    public List<Recipient> getRecipientList(Long userId) {
        return recipientRepository.findByUserId(userId);
    }

    @Override
    public Recipient addRecipient(Recipient recipient, Long userId) {
        recipient.setUserId(userId);
        recipient.setCreatedAt(LocalDateTime.now());
        return recipientRepository.save(recipient);
    }

    @Override
    public Recipient updateRecipient(Long id, Recipient recipientDetails, Long userId) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with id: " + id));
        if (!recipient.getUserId().equals(userId)) {
            throw new ForbiddenException("Forbidden: recipient does not belong to user");
        }
        recipient.setFullName(recipientDetails.getFullName());
        recipient.setIban(recipientDetails.getIban());
        recipient.setBank(recipientDetails.getBank());
        return recipientRepository.save(recipient);
    }

    @Override
    public void deleteRecipient(Long id, Long userId) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with id: " + id));
        if (!recipient.getUserId().equals(userId)) {
            throw new ForbiddenException("Forbidden: recipient does not belong to user");
        }
        recipientRepository.deleteById(id);
    }

    @Override
    public Optional<Recipient> getByIban(String iban) {
        return recipientRepository.findByIban(iban);
    }
}
