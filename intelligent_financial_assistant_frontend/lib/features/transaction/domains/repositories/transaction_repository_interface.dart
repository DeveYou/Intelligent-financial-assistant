import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';

abstract class TransactionRepositoryInterface {
  Future<ApiResponse> getTransactions({int limit = 20});
  Future<ApiResponse> getTransactionDetails(String transactionId);
  Future<ApiResponse> getAccountBalance();
  Future<ApiResponse> sendMoney(TransactionModel transaction);
  Future<ApiResponse> getLastTransaction();
}