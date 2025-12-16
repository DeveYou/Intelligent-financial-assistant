package com.khaoula.transactionsservice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions", uniqueConstraints = {
                @UniqueConstraint(name = "uk_transaction_reference", columnNames = "reference")
}, indexes = {
                @Index(name = "idx_transaction_bank_account", columnList = "bank_account_id"),
                @Index(name = "idx_transaction_date", columnList = "tx_date")
})
@Data
public class Transaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(name = "bank_account_id", nullable = false)
        private Long bankAccountId;

        @Column(name = "reference", nullable = false, length = 100)
        private String reference;

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false, length = 20)
        private TransactionType type;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false, length = 20)
        private TransactionStatus status;

        @Column(name = "amount", nullable = false, precision = 19, scale = 4)
        private BigDecimal amount;

        @Column(name = "recipient_id")
        private Long recipientId;

        @Column(name = "recipient_name")
        private String recipientName;

        @Column(name = "recipient_iban")
        private String recipientIban;

        @Column(name = "reason")
        private String reason;

        @Column(name = "tx_date", nullable = false)
        private OffsetDateTime date;

        public Transaction() {
        }
}
