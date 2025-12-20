package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import java.time.LocalDate;
import java.util.Date;

public class BankAccountResponseDTO {
    private Long id;
    private String iban;
    private Double balance;
    private LocalDate expirationDate;
    private Boolean isActive;
    private Boolean isPaymentByCard;
    private Boolean isWithdrawal;
    private Boolean isOnlinePayment;
    private Boolean isContactless;
    private Date createdAt;
    private AccountType type;
    private Double overDraft;
    private Double interestRate;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsPaymentByCard() {
        return isPaymentByCard;
    }

    public void setIsPaymentByCard(Boolean isPaymentByCard) {
        this.isPaymentByCard = isPaymentByCard;
    }

    public Boolean getIsWithdrawal() {
        return isWithdrawal;
    }

    public void setIsWithdrawal(Boolean isWithdrawal) {
        this.isWithdrawal = isWithdrawal;
    }

    public Boolean getIsOnlinePayment() {
        return isOnlinePayment;
    }

    public void setIsOnlinePayment(Boolean isOnlinePayment) {
        this.isOnlinePayment = isOnlinePayment;
    }

    public Boolean getIsContactless() {
        return isContactless;
    }

    public void setIsContactless(Boolean isContactless) {
        this.isContactless = isContactless;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Double getOverDraft() {
        return overDraft;
    }

    public void setOverDraft(Double overDraft) {
        this.overDraft = overDraft;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
