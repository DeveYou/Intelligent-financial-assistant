package com.khaoula.transactionsservice.repository;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void findByReference_Success() {
        Transaction transaction = new Transaction();
        transaction.setUserId(1L);
        transaction.setBankAccountId(1L);
        transaction.setReference("TXN-12345678");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(OffsetDateTime.now());
        entityManager.persist(transaction);
        entityManager.flush();

        Optional<Transaction> found = transactionRepository.findByReference("TXN-12345678");

        assertTrue(found.isPresent());
        assertEquals("TXN-12345678", found.get().getReference());
    }

    @Test
    void findByUserId_Success() {
        Transaction transaction = new Transaction();
        transaction.setUserId(1L);
        transaction.setBankAccountId(1L);
        transaction.setReference("TXN-12345678");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(OffsetDateTime.now());
        entityManager.persist(transaction);
        entityManager.flush();

        List<Transaction> found = transactionRepository.findByUserId(1L);

        assertFalse(found.isEmpty());
        assertEquals(1L, found.get(0).getUserId());
    }
}
