package com.khaoula.recipientservice.repository;

import com.khaoula.recipientservice.model.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    List<Recipient> findByUserId(Long userId);
    Optional<Recipient> findByIban(String iban);
}
