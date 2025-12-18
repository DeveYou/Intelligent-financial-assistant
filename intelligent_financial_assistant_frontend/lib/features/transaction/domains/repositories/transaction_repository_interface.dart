import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';

/// Interface defining the contract for transaction-related data operations.
abstract class TransactionRepositoryInterface {
  /// Retrieves a list of transactions.
  ///
  /// [limit] specifies the maximum number of transactions to fetch (default is 20).
  /// Returns an [ApiResponse] containing the list of transactions.
  Future<ApiResponse> getTransactions({int limit = 20});

  /// Retrieves detailed information for a specific transaction by [transactionId].
  Future<ApiResponse> getTransactionDetails(String transactionId);

  /// Fetches the current account balance.
  Future<ApiResponse> getAccountBalance();

  /// Initiates a money transfer based on the provided [transaction] data.
  Future<ApiResponse> sendMoney(TransactionModel transaction);

  /// Retrieves the most recent transaction.
  Future<ApiResponse> getLastTransaction();
}