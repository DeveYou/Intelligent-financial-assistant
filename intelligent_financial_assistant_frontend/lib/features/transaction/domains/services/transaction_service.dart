import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/repositories/transaction_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/services/transaction_service_interface.dart';

class TransactionService implements TransactionServiceInterface {
  final TransactionRepositoryInterface transactionRepository;

  TransactionService({required this.transactionRepository});

  @override
  Future<ApiResponse> getTransactions({int limit = 20}) async {
    return await transactionRepository.getTransactions(limit: limit);
  }

  @override
  Future<ApiResponse> getTransactionDetails(String transactionId) async {
    return await transactionRepository.getTransactionDetails(transactionId);
  }

  @override
  Future<ApiResponse> getAccountBalance() async {
    return await transactionRepository.getAccountBalance();
  }

  @override
  Future<ApiResponse> sendMoney(TransactionModel transaction) async {
    // TODO: Backend performance check required. Transfer endpoint can be slow (>60s) causing timeouts.
    return await transactionRepository.sendMoney(transaction);
  }

  @override
  Future<ApiResponse> getLastTransaction() async {
    return await transactionRepository.getLastTransaction();
  }
}