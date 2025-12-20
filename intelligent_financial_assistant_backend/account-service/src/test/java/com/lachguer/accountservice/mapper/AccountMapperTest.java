package com.lachguer.accountservice.mapper;

import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountMapperTest {

    private final AccountMapper accountMapper = new AccountMapper();

    @Test
    public void fromBankAccount_shouldMapFullName_whenUserIsPresent() {
        CurrentAccount account = new CurrentAccount();
        account.setId(1L);
        account.setBalance(1000.0);
        
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        account.setUser(user);

        BankAccountResponseDTO responseDTO = accountMapper.fromBankAccount(account);

        Assertions.assertNotNull(responseDTO);
        Assertions.assertEquals("John Doe", responseDTO.getFullName());
    }

    @Test
    public void fromBankAccount_shouldNotMapFullName_whenUserIsNotPresent() {
        CurrentAccount account = new CurrentAccount();
        account.setId(1L);
        
        BankAccountResponseDTO responseDTO = accountMapper.fromBankAccount(account);

        Assertions.assertNotNull(responseDTO);
        Assertions.assertNull(responseDTO.getFullName());
    }
}
