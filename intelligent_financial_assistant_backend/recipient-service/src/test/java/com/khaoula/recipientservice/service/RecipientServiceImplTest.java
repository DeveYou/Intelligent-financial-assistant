package com.khaoula.recipientservice.service;

import com.khaoula.recipientservice.exception.ForbiddenException;
import com.khaoula.recipientservice.exception.ResourceNotFoundException;
import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientServiceImplTest {

    @Mock
    private RecipientRepository recipientRepository;

    @InjectMocks
    private RecipientServiceImpl recipientService;

    private Recipient recipient;

    @BeforeEach
    void setUp() {
        recipient = new Recipient();
        recipient.setId(1L);
        recipient.setUserId(1L);
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setBank("MyBank");
        recipient.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getRecipientList_Success() {
        when(recipientRepository.findByUserId(1L)).thenReturn(Collections.singletonList(recipient));

        List<Recipient> result = recipientService.getRecipientList(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void addRecipient_Success() {
        when(recipientRepository.save(any(Recipient.class))).thenReturn(recipient);

        Recipient result = recipientService.addRecipient(recipient, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(recipientRepository, times(1)).save(any(Recipient.class));
    }

    @Test
    void updateRecipient_Success() {
        Recipient updatedDetails = new Recipient();
        updatedDetails.setFullName("Jane Doe");
        updatedDetails.setIban("FR7698765432109876543210987");
        updatedDetails.setBank("OtherBank");

        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(recipientRepository.save(any(Recipient.class))).thenReturn(recipient);

        Recipient result = recipientService.updateRecipient(1L, updatedDetails, 1L);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getFullName());
        assertEquals("OtherBank", result.getBank());
    }

    @Test
    void updateRecipient_NotFound() {
        Recipient updatedDetails = new Recipient();
        when(recipientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipientService.updateRecipient(1L, updatedDetails, 1L));
    }

    @Test
    void updateRecipient_Forbidden() {
        Recipient updatedDetails = new Recipient();
        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));

        assertThrows(ForbiddenException.class, () -> recipientService.updateRecipient(1L, updatedDetails, 2L));
    }

    @Test
    void deleteRecipient_Success() {
        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));
        doNothing().when(recipientRepository).deleteById(1L);

        recipientService.deleteRecipient(1L, 1L);

        verify(recipientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRecipient_NotFound() {
        when(recipientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipientService.deleteRecipient(1L, 1L));
    }

    @Test
    void deleteRecipient_Forbidden() {
        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));

        assertThrows(ForbiddenException.class, () -> recipientService.deleteRecipient(1L, 2L));
    }

    @Test
    void getByIban_Success() {
        when(recipientRepository.findByIban("FR7612345678901234567890123")).thenReturn(Optional.of(recipient));

        Optional<Recipient> result = recipientService.getByIban("FR7612345678901234567890123");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getFullName());
    }
}
