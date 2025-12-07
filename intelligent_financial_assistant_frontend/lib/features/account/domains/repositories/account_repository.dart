import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/repositories/account_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class AccountRepository implements AccountRepositoryInterface{
  final DioClient? dioClient;

  AccountRepository({required this.dioClient});

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
  Future<ApiResponse> deleteAccount() async {
    try {
      final response = await dioClient!.post(AppConstants.deleteAccountUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> getAccountDetails() async {
    try {
      final response = await dioClient!.get(AppConstants.accountUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> updateAccountDetails(AccountModel accountModel) async {
    try {
      final response = await dioClient!.post(
        AppConstants.updateAccountUri,
        data: {
          'is_active': accountModel.isActive,
          'allow_card_payment': accountModel.isPaymentByCard,
          'allow_withdrawal': accountModel.isWithdrawal,
          'allow_online_payment': accountModel.isOnlinePayment,
          'allow_contactless': accountModel.isContactless,
        },
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> getRecentTransactions({int limit = 10}) async {
    try {
      final response = await dioClient!.get('${AppConstants.getTransactionsUri}?limit=$limit');
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> resendBankCardCode() async {
    try {
      final response = await dioClient!.post(AppConstants.resendCardCodeUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }
  
  
}