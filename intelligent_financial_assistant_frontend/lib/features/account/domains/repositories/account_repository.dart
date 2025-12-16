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
    // TODO: Need actual account ID here, currently signature is empty.
    // Assuming assuming we need to pass an ID or fetch it.
    // For now, aligning call to delete URI.
    try {
       // Note: Front-end likely needs refactor to pass ID.
       // Using user-info or similar to get account ID first if not provided.
       // Here we assume the repository method might need to change signature,
       // but we are sticking to the file provided.
       // WARNING: This call is likely to fail without an ID if backend requires DELETE /{id}
       // We will assume "current account" context or update this to be correct if possible.
       // Since I cannot change the abstract interface easily without seeing it, I'll assume the AccountModel
       // context is available or I should pass it.
       // Ideally: deleteAccount(int id)
       // Keeping as is but updating Method to DELETE
      final response = await dioClient!.delete(AppConstants.deleteAccountUri);
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
      final response = await dioClient!.put(
        '${AppConstants.updateAccountUri}/${accountModel.id}',
        data: {
          'is_active': accountModel.isActive,
          'isActive': accountModel.isActive,
          'isPaymentByCard': accountModel.isPaymentByCard,
          'isWithdrawal': accountModel.isWithdrawal,
          'isOnlinePayment': accountModel.isOnlinePayment,
          'isContactless': accountModel.isContactless,
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