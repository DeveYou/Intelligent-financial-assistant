package com.khaoula.transactionsservice.client;

import com.khaoula.transactionsservice.dto.BankAccountDTO;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class BankAccountClientFallback implements BankAccountClient {

    private BankAccountDTO createFallbackAccount(String rib) {
        BankAccountDTO fallback = new BankAccountDTO();
        fallback.setRib(rib);
        fallback.setBalance(null); // Indique que le solde est indisponible
        fallback.setActive(false); // Indique que le statut est incertain
        fallback.setUserId(null); // ID utilisateur inconnu
        return fallback;
    }

    @Override
    public BankAccountDTO getAccountByRib(String rib) {
        System.err.println("Fallback for getAccountByRib: service is down. RIB: " + rib);
        return createFallbackAccount(rib);
    }

    @Override
    public void deposit(DepositRequestDTO depositRequest) {
        System.err.println("Fallback for deposit: service is down. Request: " + depositRequest);
        throw new RuntimeException("Bank account service is unavailable. Deposit failed.");
    }

    @Override
    public void withdraw(WithdrawalRequestDTO withdrawalRequest) {
        System.err.println("Fallback for withdraw: service is down. Request: " + withdrawalRequest);
        throw new RuntimeException("Bank account service is unavailable. Withdrawal failed.");
    }

    @Override
    public void transfer(TransferRequestDTO transferRequest) {
        System.err.println("Fallback for transfer: service is down. Request: " + transferRequest);
        throw new RuntimeException("Bank account service is unavailable. Transfer failed.");
    }
}

