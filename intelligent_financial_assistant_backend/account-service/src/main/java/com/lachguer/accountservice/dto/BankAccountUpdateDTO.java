package com.lachguer.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author radouane
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountUpdateDTO {
    private Double balance;
    private Boolean isActive;
    private Boolean isPaymentByCard;
    private Boolean isWithdrawal;
    private Boolean isOnlinePayment;
    private Boolean isContactless;
    private Double overDraft;  // Pour CurrentAccount
    private Double interestRate; // Pour SavingAccount
}
