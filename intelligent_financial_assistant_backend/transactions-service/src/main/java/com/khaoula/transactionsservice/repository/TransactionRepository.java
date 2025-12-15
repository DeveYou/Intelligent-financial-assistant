package com.khaoula.transactionsservice.repository;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReference(String reference);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByBankAccountId(Long bankAccountId);

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    Page<Transaction> findByBankAccountId(Long bankAccountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:userId IS NULL OR t.userId = :userId) AND " +
            "(:bankAccountId IS NULL OR t.bankAccountId = :bankAccountId) AND " +
            "(:type IS NULL OR t.type = :type) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(cast(:startDate as timestamp) IS NULL OR t.date >= :startDate) AND " +
            "(cast(:endDate as timestamp) IS NULL OR t.date <= :endDate)")
    Page<Transaction> findByFilters(@Param("userId") Long userId,
                                    @Param("bankAccountId") Long bankAccountId,
                                    @Param("type") TransactionType type,
                                    @Param("status") TransactionStatus status,
                                    @Param("startDate") OffsetDateTime startDate,
                                    @Param("endDate") OffsetDateTime endDate,
                                    Pageable pageable);

    List<Transaction> findByUserIdAndDateBetween(Long userId, OffsetDateTime startDate, OffsetDateTime endDate);

    Long countByUserId(Long userId);

    Long countByStatus(TransactionStatus status);
}
