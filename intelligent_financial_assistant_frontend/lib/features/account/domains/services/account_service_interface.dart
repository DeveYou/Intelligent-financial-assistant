import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';

abstract class AccountServiceInterface {
  Future<dynamic> getAccountDetails();
  Future<ApiResponse> updateAccountDetails(AccountModel accountModel);
  Future<ApiResponse> deleteAccount();
  Future<ApiResponse> changePassword(String oldPassword, String newPassword);
  Future<ApiResponse> activateAccount();
  Future<ApiResponse> resendBankCardCode();
  Future<ApiResponse> getRecentTransactions();
}