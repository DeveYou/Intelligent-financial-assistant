package com.khaoula.recipientservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecipientTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void defaultConstructor_CreatesInstance() {
        Recipient recipient = new Recipient();
        assertNotNull(recipient);
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        Recipient recipient = new Recipient();
        LocalDateTime now = LocalDateTime.now();

        recipient.setId(1L);
        assertEquals(1L, recipient.getId());

        recipient.setFullName("John Doe");
        assertEquals("John Doe", recipient.getFullName());

        recipient.setIban("FR7612345678901234567890123");
        assertEquals("FR7612345678901234567890123", recipient.getIban());

        recipient.setBank("MyBank");
        assertEquals("MyBank", recipient.getBank());

        recipient.setUserId(100L);
        assertEquals(100L, recipient.getUserId());

        recipient.setCreatedAt(now);
        assertEquals(now, recipient.getCreatedAt());
    }

    @Test
    void validation_ValidRecipient_NoViolations() {
        Recipient recipient = new Recipient();
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setBank("MyBank");
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_BlankFullName_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName("");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Full name is required")));
    }

    @Test
    void validation_NullFullName_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName(null);
        recipient.setIban("FR7612345678901234567890123");
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validation_FullNameTooLong_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName("a".repeat(101));
        recipient.setIban("FR7612345678901234567890123");
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot exceed 100 characters")));
    }

    @Test
    void validation_BlankIban_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName("John Doe");
        recipient.setIban("");
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("IBAN is required")));
    }

    @Test
    void validation_IbanTooShort_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName("John Doe");
        recipient.setIban("FR123");
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 15 and 34 characters")));
    }

    @Test
    void validation_IbanTooLong_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName("John Doe");
        recipient.setIban("FR" + "1".repeat(35));
        recipient.setUserId(1L);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 15 and 34 characters")));
    }

    @Test
    void validation_NullUserId_HasViolation() {
        Recipient recipient = new Recipient();
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setUserId(null);

        Set<ConstraintViolation<Recipient>> violations = validator.validate(recipient);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("User ID is required")));
    }

    @Test
    void allFields_CanBeSetAndRetrieved() {
        Recipient recipient = new Recipient();
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        recipient.setId(123L);
        recipient.setFullName("Jane Doe");
        recipient.setIban("FR7698765432109876543210987");
        recipient.setBank("Test Bank");
        recipient.setUserId(456L);
        recipient.setCreatedAt(timestamp);

        assertEquals(123L, recipient.getId());
        assertEquals("Jane Doe", recipient.getFullName());
        assertEquals("FR7698765432109876543210987", recipient.getIban());
        assertEquals("Test Bank", recipient.getBank());
        assertEquals(456L, recipient.getUserId());
        assertEquals(timestamp, recipient.getCreatedAt());
    }
}
