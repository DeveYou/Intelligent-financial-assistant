package com.lachguer.accountservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CURRENT_ACCOUNT")
public class CurrentAccount extends BankAccount {
    private Double overDraft;

    public CurrentAccount() {
        super();
    }

    public CurrentAccount(Double overDraft) {
        this.overDraft = overDraft;
    }

    public Double getOverDraft() {
        return overDraft;
    }

    public void setOverDraft(Double overDraft) {
        this.overDraft = overDraft;
    }
}
