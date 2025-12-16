import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/repositories/account_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service_interface.dart';

class AccountService implements AccountServiceInterface{

  final AccountRepositoryInterface accountRepository;

  AccountService({required this.accountRepository});

  @override
  Future<ApiResponse> activateAccount() {
    // TODO: implement activateAccount
    throw UnimplementedError();
  }

  @override
  Future<ApiResponse> changePassword(String oldPassword, String newPassword) {
    // TODO: implement changePassword
    throw UnimplementedError();
  }

  @override
  Future<ApiResponse> deleteAccount() {
    // TODO: implement deleteAccount
    throw UnimplementedError();
  }

  @override
  Future<ApiResponse> getAccountDetails() async {
    return await accountRepository.getAccountDetails();
  }

  @override
  Future<ApiResponse> updateAccountDetails(AccountModel accountModel) async {
    return await accountRepository.updateAccountDetails(accountModel);
  }

  @override
  Future<ApiResponse> getRecentTransactions() async {
    return await accountRepository.getRecentTransactions(limit: 10);
  }

  @override
  Future<ApiResponse> resendBankCardCode() async {
    return await accountRepository.resendBankCardCode();
  }

}