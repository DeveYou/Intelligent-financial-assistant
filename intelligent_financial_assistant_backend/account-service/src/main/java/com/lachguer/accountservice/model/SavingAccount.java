package com.lachguer.accountservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SAVING_ACCOUNT")
public class SavingAccount extends BankAccount {
    private Double interestRate;

    public SavingAccount() {
        super();
    }

    public SavingAccount(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }
}
