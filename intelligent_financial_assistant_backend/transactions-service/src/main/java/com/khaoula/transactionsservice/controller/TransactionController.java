package com.khaoula.transactionsservice.controller;

import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody DepositRequestDTO request) {
        TransactionResponseDTO response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody WithdrawalRequestDTO request) {
        TransactionResponseDTO response = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO request) {
        TransactionResponseDTO response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/by-account/{bankAccountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getHistoryByAccount(@PathVariable String bankAccountId) {
        List<TransactionResponseDTO> history = transactionService.getHistoryByAccount(bankAccountId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/by-reference/{reference}")
    public ResponseEntity<TransactionResponseDTO> getByReference(@PathVariable String reference) {
        TransactionResponseDTO response = transactionService.getByReference(reference);
        return ResponseEntity.ok(response);
    }
}

