package com.khaoula.transactionsservice.repository;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReference(String reference);

    List<Transaction> findByBankAccountIdOrderByDateDesc(String bankAccountId);

    @Query("""
            SELECT t FROM Transaction t
            WHERE (:type IS NULL OR t.type = :type)
              AND (:bankAccountId IS NULL OR LOWER(t.bankAccountId) LIKE LOWER(CONCAT('%', :bankAccountId, '%')))
              AND (:reference IS NULL OR LOWER(t.reference) LIKE LOWER(CONCAT('%', :reference, '%')))
              AND (
                    :search IS NULL OR
                    LOWER(t.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(t.bankAccountId) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(COALESCE(t.receiver, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(COALESCE(t.reason, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
              AND (:startDate IS NULL OR t.date >= :startDate)
              AND (:endDate IS NULL OR t.date <= :endDate)
            ORDER BY t.date DESC
            """)
    List<Transaction> searchTransactions(
            @Param("type") TransactionType type,
            @Param("bankAccountId") String bankAccountId,
            @Param("reference") String reference,
            @Param("search") String search,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
}
