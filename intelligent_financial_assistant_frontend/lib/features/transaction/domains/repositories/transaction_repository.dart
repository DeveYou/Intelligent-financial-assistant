import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/repositories/transaction_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class TransactionRepository implements TransactionRepositoryInterface {
  final DioClient? dioClient;

  TransactionRepository({required this.dioClient});

  @override
  Future<ApiResponse> getTransactions({int limit = 20}) async {
    try {
      // Backend: GET /user/transactions/my-transactions
      final response = await dioClient!.get('${AppConstants.getTransactionsUri}?limit=$limit');
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> getTransactionDetails(String transactionId) async {
    try {
      // Backend: GET /{id} (Needs to be added to Backend)
      // Or use GET /reference
      // Assuming I add GET /{id} to the backend as per instructions.
      final response = await dioClient!.get('${AppConstants.transactionBaseUri}/$transactionId');
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> getAccountBalance() async {
     // TODO: Backend does not have a direct "get balance" endpoint that is not the account details one.
     // Account details returns balance.
     // Using accountUri to get details and parse balance?
     // Or assuming the backend won't change.
     // Previous code called: accountUri + '/balance' which didn't exist in my audit (only POST).
     // I will change this to getAccountDetails behavior or remove if unused, 
     // but since it returns ApiResponse, I'll redirect to getAccountDetails endpoint which returns DTO with balance.
    try {
      final response = await dioClient!.get(AppConstants.accountUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> sendMoney(TransactionModel transaction) async {
    try {
      // Backend: POST /transfer (Check TransactionType)
      String uri;
      if (transaction.type == 'TRANSFER') {
        uri = AppConstants.transferTransactionUri;
      } else if (transaction.type == 'DEPOSIT') {
         uri = AppConstants.depositTransactionUri;
      } else if (transaction.type == 'WITHDRAWAL') {
         uri = AppConstants.withdrawalTransactionUri;
      } else {
        uri = AppConstants.transferTransactionUri; // Default
      }
      
      final response = await dioClient!.post(
        uri,
        data: transaction.toJson(),
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> getLastTransaction() async {
    try {
      final response = await dioClient!.get('${AppConstants.getTransactionsUri}?limit=1');
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }
}