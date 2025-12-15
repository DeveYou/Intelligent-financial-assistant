package com.khaoula.recipientservice.service;

import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipientServiceImplTest {

    @Mock
    private RecipientRepository recipientRepository;

    @InjectMocks
    private RecipientServiceImpl recipientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRecipientList_callsRepositoryWithUserId() {
        Recipient r = new Recipient();
        r.setBank("Bank");
        r.setIban("IBAN");
        r.setFullName("Full");
        r.setCreatedAt(LocalDateTime.now());
        r.setUserId(1L);
        when(recipientRepository.findByUserId(1L)).thenReturn(List.of(r));

        List<Recipient> list = recipientService.getRecipientList(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        verify(recipientRepository, times(1)).findByUserId(1L);
    }

    @Test
    void addRecipient_setsUserIdAndCreatedAt_andSaves() {
        Recipient input = new Recipient();
        input.setFullName("Test");
        input.setIban("IBAN123");

        when(recipientRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Recipient saved = recipientService.addRecipient(input, 2L);

        assertNotNull(saved.getUserId());
        assertEquals(2L, saved.getUserId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Test", saved.getFullName());
        verify(recipientRepository, times(1)).save(any());
    }

    @Test
    void updateRecipient_notFound_throws() {
        when(recipientRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> recipientService.updateRecipient(99L, new Recipient(), 1L));
        assertTrue(ex.getMessage().contains("Recipient not found"));
    }

    @Test
    void deleteRecipient_delegatesToRepository() {
        Recipient r = new Recipient();
        r.setUserId(1L);
        when(recipientRepository.findById(5L)).thenReturn(Optional.of(r));
        doNothing().when(recipientRepository).deleteById(5L);
        recipientService.deleteRecipient(5L, 1L);
        verify(recipientRepository, times(1)).deleteById(5L);
    }
}
