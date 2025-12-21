package com.lachguer.accountservice.mapper;

import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
    }

    @Test
    void fromBankAccount_CurrentAccount() {
        CurrentAccount account = new CurrentAccount();
        account.setId(1L);
        account.setBalance(1000.0);
        account.setAccountType(AccountType.CURRENT_ACCOUNT);
        account.setOverDraft(500.0);
        
        BankAccountResponseDTO dto = accountMapper.fromBankAccount(account);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1000.0, dto.getBalance());
        assertEquals(AccountType.CURRENT_ACCOUNT, dto.getType());
        assertEquals(500.0, dto.getOverDraft());
    }

    @Test
    void fromBankAccount_SavingAccount() {
        SavingAccount account = new SavingAccount();
        account.setId(2L);
        account.setBalance(2000.0);
        account.setAccountType(AccountType.SAVING_ACCOUNT);
        account.setInterestRate(3.5);
        
        BankAccountResponseDTO dto = accountMapper.fromBankAccount(account);
        
        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals(2000.0, dto.getBalance());
        assertEquals(AccountType.SAVING_ACCOUNT, dto.getType());
        assertEquals(3.5, dto.getInterestRate());
    }
    
    @Test
    void fromBankAccount_OtherSubclass() {
        com.lachguer.accountservice.model.BankAccount other = new com.lachguer.accountservice.model.BankAccount() {};
        other.setRib("OTHER");
        other.setAccountType(AccountType.CURRENT_ACCOUNT);
        
        BankAccountResponseDTO dto = accountMapper.fromBankAccount(other);
        assertNotNull(dto);
        assertEquals("OTHER", dto.getIban());
    }
    
    @Test
    void fromBankAccount_Null() {
        assertNull(accountMapper.fromBankAccount(null));
    }
}
