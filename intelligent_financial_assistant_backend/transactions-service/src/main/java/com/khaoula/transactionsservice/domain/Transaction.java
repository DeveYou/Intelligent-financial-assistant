package com.khaoula.transactionsservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions", uniqueConstraints = {
                @UniqueConstraint(name = "uk_transaction_reference", columnNames = "reference")
}, indexes = {
                @Index(name = "idx_transaction_bank_account", columnList = "bank_account_id"),
                @Index(name = "idx_transaction_date", columnList = "tx_date")
})
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

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getUserId() {
                return userId;
        }

        public void setUserId(Long userId) {
                this.userId = userId;
        }

        public Long getBankAccountId() {
                return bankAccountId;
        }

        public void setBankAccountId(Long bankAccountId) {
                this.bankAccountId = bankAccountId;
        }

        public String getReference() {
                return reference;
        }

        public void setReference(String reference) {
                this.reference = reference;
        }

        public TransactionType getType() {
                return type;
        }

        public void setType(TransactionType type) {
                this.type = type;
        }

        public TransactionStatus getStatus() {
                return status;
        }

        public void setStatus(TransactionStatus status) {
                this.status = status;
        }

        public BigDecimal getAmount() {
                return amount;
        }

        public void setAmount(BigDecimal amount) {
                this.amount = amount;
        }

        public Long getRecipientId() {
                return recipientId;
        }

        public void setRecipientId(Long recipientId) {
                this.recipientId = recipientId;
        }

        public String getRecipientName() {
                return recipientName;
        }

        public void setRecipientName(String recipientName) {
                this.recipientName = recipientName;
        }

        public String getRecipientIban() {
                return recipientIban;
        }

        public void setRecipientIban(String recipientIban) {
                this.recipientIban = recipientIban;
        }

        public String getReason() {
                return reason;
        }

        public void setReason(String reason) {
                this.reason = reason;
        }

        public OffsetDateTime getDate() {
                return date;
        }

        public void setDate(OffsetDateTime date) {
                this.date = date;
        }
}
