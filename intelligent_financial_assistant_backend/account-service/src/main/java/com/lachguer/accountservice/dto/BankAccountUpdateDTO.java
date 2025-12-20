package com.lachguer.accountservice.dto;

public class BankAccountUpdateDTO {
    private Double balance;
    private Boolean isActive;
    private Boolean isPaymentByCard;
    private Boolean isWithdrawal;
    private Boolean isOnlinePayment;
    private Boolean isContactless;
    private Double overDraft;  // Pour CurrentAccount
    private Double interestRate; // Pour SavingAccount

    public BankAccountUpdateDTO() {
    }

    public BankAccountUpdateDTO(Double balance, Boolean isActive, Boolean isPaymentByCard, Boolean isWithdrawal, Boolean isOnlinePayment, Boolean isContactless, Double overDraft, Double interestRate) {
        this.balance = balance;
        this.isActive = isActive;
        this.isPaymentByCard = isPaymentByCard;
        this.isWithdrawal = isWithdrawal;
        this.isOnlinePayment = isOnlinePayment;
        this.isContactless = isContactless;
        this.overDraft = overDraft;
        this.interestRate = interestRate;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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
}
