package com.lachguer.accountservice.repository;

import com.lachguer.accountservice.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUserId(Long userId);

    long count();

    BankAccount findByRib(String rib);

    @org.springframework.data.jpa.repository.Query("SELECT a.accountType, COUNT(a) FROM BankAccount a GROUP BY a.accountType")
    List<Object[]> countTotalAccountsByType();
}
