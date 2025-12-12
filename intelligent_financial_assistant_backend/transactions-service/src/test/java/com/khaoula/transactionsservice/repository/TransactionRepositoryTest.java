package com.khaoula.transactionsservice.repository;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void findByReference_shouldReturnTransaction_whenExists() {
        Transaction tx = buildTransaction("ACC-1", "REF-1", OffsetDateTime.now().minusDays(1));
        transactionRepository.save(tx);

        Optional<Transaction> found = transactionRepository.findByReference("REF-1");

        assertTrue(found.isPresent());
        assertEquals("REF-1", found.get().getReference());
    }

    @Test
    void findByReference_shouldReturnEmpty_whenNotExists() {
        Optional<Transaction> found = transactionRepository.findByReference("UNKNOWN");
        assertTrue(found.isEmpty());
    }

    @Test
    void findByBankAccountIdOrderByDateDesc_shouldReturnSortedList() {
        Transaction older = buildTransaction("ACC-1", "REF-OLD", OffsetDateTime.now().minusDays(2));
        Transaction newer = buildTransaction("ACC-1", "REF-NEW", OffsetDateTime.now());
        Transaction otherAccount = buildTransaction("ACC-2", "REF-OTHER", OffsetDateTime.now());

        transactionRepository.saveAll(List.of(older, newer, otherAccount));

        List<Transaction> result = transactionRepository.findByBankAccountIdOrderByDateDesc("ACC-1");

        assertEquals(2, result.size());
        assertEquals("REF-NEW", result.get(0).getReference());
        assertEquals("REF-OLD", result.get(1).getReference());
    }

    private Transaction buildTransaction(String bankAccountId, String reference, OffsetDateTime date) {
        Transaction tx = new Transaction();
        tx.setBankAccountId(bankAccountId);
        tx.setReference(reference);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(BigDecimal.TEN);
        tx.setDate(date);
        tx.setUserId(1L);
        tx.setStatus(com.khaoula.transactionsservice.domain.TransactionStatus.COMPLETED);
        return tx;
    }
}
