import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';

abstract class AccountRepositoryInterface {
  Future<dynamic> getAccountDetails();
  Future<ApiResponse> updateAccountDetails(AccountModel updatedModel);
  Future<ApiResponse> deleteAccount();
  Future<ApiResponse> changePassword(String oldPassword, String newPassword);
  Future<ApiResponse> activateAccount();
  Future<ApiResponse> resendBankCardCode();
  Future<ApiResponse> getRecentTransactions({int limit = 10});
}