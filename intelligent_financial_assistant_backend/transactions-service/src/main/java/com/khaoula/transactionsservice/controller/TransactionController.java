package com.khaoula.transactionsservice.controller;

import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction API", description = "API for managing financial transactions (deposits, withdrawals, transfers)")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Make a deposit", description = "Creates a deposit transaction and updates the account balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposit successful",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., invalid amount, account not found)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid authentication)")
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @Parameter(in = ParameterIn.HEADER, name = "X-Authenticated-User-Id", required = true, description = "Authenticated user's ID")
            @RequestHeader("X-Authenticated-User-Id") Long authenticatedUserId,
            @Valid @RequestBody DepositRequestDTO request
    ) {
        TransactionResponseDTO response = transactionService.deposit(authenticatedUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Make a withdrawal", description = "Creates a withdrawal transaction and updates the account balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Withdrawal successful",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., insufficient funds)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @Parameter(in = ParameterIn.HEADER, name = "X-Authenticated-User-Id", required = true, description = "Authenticated user's ID")
            @RequestHeader("X-Authenticated-User-Id") Long authenticatedUserId,
            @Valid @RequestBody WithdrawalRequestDTO request
    ) {
        TransactionResponseDTO response = transactionService.withdraw(authenticatedUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Make a transfer", description = "Creates a transfer transaction between two accounts and updates their balances.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transfer successful",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., insufficient funds, same accounts)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Parameter(in = ParameterIn.HEADER, name = "X-Authenticated-User-Id", required = true, description = "Authenticated user's ID")
            @RequestHeader("X-Authenticated-User-Id") Long authenticatedUserId,
            @Valid @RequestBody TransferRequestDTO request
    ) {
        TransactionResponseDTO response = transactionService.transfer(authenticatedUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get transaction history for an account", description = "Retrieves a list of all transactions for a specific bank account, sorted by date descending.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved history"),
            @ApiResponse(responseCode = "404", description = "Bank account not found")
    })
    @GetMapping("/by-account/{bankAccountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getHistoryByAccount(
            @Parameter(description = "RIB of the bank account") @PathVariable String bankAccountId
    ) {
        List<TransactionResponseDTO> history = transactionService.getHistoryByAccount(bankAccountId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get a transaction by its reference", description = "Retrieves a single transaction using its unique reference.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction"),
            @ApiResponse(responseCode = "404", description = "Transaction not found with the given reference")
    })
    @GetMapping("/by-reference/{reference}")
    public ResponseEntity<TransactionResponseDTO> getByReference(
            @Parameter(description = "Unique reference of the transaction") @PathVariable String reference
    ) {
        TransactionResponseDTO response = transactionService.getByReference(reference);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search for transactions", description = "Performs an advanced search for transactions based on multiple optional criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful")
    })
    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponseDTO>> search(
            @Parameter(description = "Filter by transaction type (DEPOSIT, WITHDRAWAL, TRANSFER)")
            @RequestParam(required = false) String type,
            @Parameter(description = "Filter by bank account ID (RIB)")
            @RequestParam(required = false) String bankAccountId,
            @Parameter(description = "Filter by transaction reference")
            @RequestParam(required = false) String reference,
            @Parameter(description = "A general search term to look for in reference, account, receiver, or reason fields")
            @RequestParam(required = false) String search,
            @Parameter(description = "Start date for the search range (ISO 8601 format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @Parameter(description = "End date for the search range (ISO 8601 format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        List<TransactionResponseDTO> results = transactionService.search(
                type, bankAccountId, reference, search, startDate, endDate
        );
        return ResponseEntity.ok(results);
    }
}
