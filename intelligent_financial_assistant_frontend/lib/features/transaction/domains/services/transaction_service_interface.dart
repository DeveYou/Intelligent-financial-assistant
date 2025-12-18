import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';

/// Service interface for transaction operations.
///
/// Defines the contract for transaction service implementations.
/// Handles transaction history, balance queries, and money transfers.
abstract class TransactionServiceInterface {
  /// Retrieves the user's transaction history.
  ///
  /// [limit] specifies the maximum number of transactions to fetch (default: 20).
  Future<ApiResponse> getTransactions({int limit = 20});
  
  /// Fetches details for a specific transaction.
  ///
  /// [transactionId] is the unique identifier of the transaction.
  Future<ApiResponse> getTransactionDetails(String transactionId);
  
  /// Retrieves the current account balance.
  Future<ApiResponse> getAccountBalance();
  
  /// Initiates a money transfer.
  ///
  /// [transaction] contains the transfer details (amount, recipient, reason, etc.).
  Future<ApiResponse> sendMoney(TransactionModel transaction);
  
  /// Retrieves the user's most recent transaction.
  Future<ApiResponse> getLastTransaction();
}