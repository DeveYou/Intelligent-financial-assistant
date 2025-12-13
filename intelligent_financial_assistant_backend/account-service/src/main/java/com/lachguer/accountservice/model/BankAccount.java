package com.lachguer.accountservice.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lachguer.accountservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
