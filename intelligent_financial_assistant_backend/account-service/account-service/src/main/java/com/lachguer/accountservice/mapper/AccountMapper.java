package com.lachguer.accountservice.mapper;

import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public BankAccountResponseDTO fromBankAccount(BankAccount bankAccount) {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        BeanUtils.copyProperties(bankAccount, responseDTO);
        responseDTO.setType(bankAccount.getAccountType());
        if (bankAccount instanceof CurrentAccount) {
            responseDTO.setOverDraft(((CurrentAccount) bankAccount).getOverDraft());
        } else if (bankAccount instanceof SavingAccount) {
            responseDTO.setInterestRate(((SavingAccount) bankAccount).getInterestRate());
        }
        return responseDTO;
    }
}

