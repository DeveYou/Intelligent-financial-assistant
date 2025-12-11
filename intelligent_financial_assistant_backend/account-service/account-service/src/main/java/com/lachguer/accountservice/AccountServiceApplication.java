package com.lachguer.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.lachguer.accountservice.repository.BankAccountRepository;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import java.util.Date;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedDatabase(BankAccountRepository bankAccountRepository) {
	    return args -> {
	        try {
	            if (bankAccountRepository.count() == 0) {
	                CurrentAccount ca = new CurrentAccount();
	                ca.setRib("RIB-TEST-1");
	                ca.setBalance(1000.0);
	                ca.setCreatedAt(new Date());
	                ca.setIsActive(true);
	                ca.setUserId(1L);
	                ca.setOverDraft(200.0);
	                ca.setAccountType(com.lachguer.accountservice.enums.AccountType.CURRENT_ACCOUNT);
	                bankAccountRepository.save(ca);

	                SavingAccount sa = new SavingAccount();
	                sa.setRib("RIB-TEST-2");
	                sa.setBalance(5000.0);
	                sa.setCreatedAt(new Date());
	                sa.setIsActive(true);
	                sa.setUserId(2L);
	                sa.setInterestRate(0.03);
	                sa.setAccountType(com.lachguer.accountservice.enums.AccountType.SAVING_ACCOUNT);
	                bankAccountRepository.save(sa);
	            }
	        } catch (Exception e) {
	            // en dev, on ignore les erreurs de seed pour ne pas bloquer le démarrage si la BDD n'est pas accessible
	            System.err.println("Seed DB skipped: " + e.getMessage());
	        }
	    };
	}
}
