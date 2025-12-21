package com.khaoula.recipientservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecipientRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void defaultConstructor_CreatesInstance() {
        RecipientRequest request = new RecipientRequest();
        assertNotNull(request);
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        RecipientRequest request = new RecipientRequest();

        request.setFullName("John Doe");
        assertEquals("John Doe", request.getFullName());

        request.setIban("FR7612345678901234567890123");
        assertEquals("FR7612345678901234567890123", request.getIban());

        request.setBank("MyBank");
        assertEquals("MyBank", request.getBank());
    }

    @Test
    void validation_ValidRequest_NoViolations() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("FR7612345678901234567890123");
        request.setBank("MyBank");

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_BlankFullName_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("");
        request.setIban("FR7612345678901234567890123");

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Full name is required")));
    }

    @Test
    void validation_NullFullName_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName(null);
        request.setIban("FR7612345678901234567890123");

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validation_FullNameTooLong_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("a".repeat(101)); // 101 characters
        request.setIban("FR7612345678901234567890123");

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot exceed 100 characters")));
    }

    @Test
    void validation_BlankIban_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("");

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("IBAN is required")));
    }

    @Test
    void validation_IbanTooShort_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("FR123"); // Too short

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 15 and 34 characters")));
    }

    @Test
    void validation_IbanTooLong_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("FR" + "1".repeat(35)); // Too long

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 15 and 34 characters")));
    }

    @Test
    void validation_BankTooLong_HasViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("FR7612345678901234567890123");
        request.setBank("a".repeat(101)); // 101 characters

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cannot exceed 100 characters")));
    }

    @Test
    void validation_NullBank_NoViolation() {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("FR7612345678901234567890123");
        request.setBank(null); // Bank is optional

        Set<ConstraintViolation<RecipientRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
