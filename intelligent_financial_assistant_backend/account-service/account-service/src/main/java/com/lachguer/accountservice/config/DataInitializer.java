package com.lachguer.accountservice.config;

import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import com.lachguer.accountservice.repository.BankAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedBankAccounts(BankAccountRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                CurrentAccount ca = new CurrentAccount();
                ca.setRib(UUID.randomUUID().toString());
                ca.setBalance(1500.0);
                ca.setIsActive(true);
                ca.setCreatedAt(new Date());
                ca.setUserId(1L);
                ca.setOverDraft(500.0);

                SavingAccount sa = new SavingAccount();
                sa.setRib(UUID.randomUUID().toString());
                sa.setBalance(2500.0);
                sa.setIsActive(true);
                sa.setCreatedAt(new Date());
                sa.setUserId(2L);
                sa.setInterestRate(3.5);

                repository.saveAll(Arrays.asList(ca, sa));
            }
        };
    }
}
