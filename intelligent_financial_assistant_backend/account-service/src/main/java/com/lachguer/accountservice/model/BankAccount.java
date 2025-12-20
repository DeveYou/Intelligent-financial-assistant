package com.lachguer.accountservice.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lachguer.accountservice.enums.AccountType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ACCOUNT_TYPE", discriminatorType = DiscriminatorType.STRING, length = 20)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "accountType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CurrentAccount.class, name = "CURRENT_ACCOUNT"),
        @JsonSubTypes.Type(value = SavingAccount.class, name = "SAVING_ACCOUNT")
})
public abstract class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String rib;
    private Double balance;
    private LocalDate expirationDate;
    private Boolean isActive;
    private Boolean isPaymentByCard;
    private Boolean isWithdrawal;
    private Boolean isOnlinePayment;
    private Boolean isContactless;
    private Date createdAt;
    private Long userId;
    @Transient
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", insertable = false, updatable = false)
    private AccountType accountType;

    public BankAccount() {
    }

    public BankAccount(Long id, String rib, Double balance, LocalDate expirationDate, Boolean isActive, Boolean isPaymentByCard, Boolean isWithdrawal, Boolean isOnlinePayment, Boolean isContactless, Date createdAt, Long userId, User user, AccountType accountType) {
        this.id = id;
        this.rib = rib;
        this.balance = balance;
        this.expirationDate = expirationDate;
        this.isActive = isActive;
        this.isPaymentByCard = isPaymentByCard;
        this.isWithdrawal = isWithdrawal;
        this.isOnlinePayment = isOnlinePayment;
        this.isContactless = isContactless;
        this.createdAt = createdAt;
        this.userId = userId;
        this.user = user;
        this.accountType = accountType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRib() {
        return rib;
    }

    public void setRib(String rib) {
        this.rib = rib;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
