import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';

/// Interface defining the contract for account management operations.
abstract class AccountRepositoryInterface {
  /// Fetches the details of the current user's account.
  Future<dynamic> getAccountDetails();

  /// Updates the account information with the provided [updatedModel].
  Future<ApiResponse> updateAccountDetails(AccountModel updatedModel);

  /// Deletes the current user's account permanently.
  Future<ApiResponse> deleteAccount();

  /// Changes the user's password.
  ///
  /// Requires the [oldPassword] for verification and the [newPassword] to set.
  Future<ApiResponse> changePassword(String oldPassword, String newPassword);

  /// Activates a dormant or suspended account.
  Future<ApiResponse> activateAccount();

  /// Requests a new code for the bank card associated with the account.
  Future<ApiResponse> resendBankCardCode();

  /// Retrieves a list of recent transactions for the account.
  ///
  /// [limit] specifies the maximum number of transactions to return (default is 10).
  Future<ApiResponse> getRecentTransactions({int limit = 10});
}