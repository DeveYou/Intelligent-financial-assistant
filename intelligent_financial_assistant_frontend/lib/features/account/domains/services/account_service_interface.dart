import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';

/// Service interface for account management operations.
///
/// Defines the contract for account service implementations.
/// Handles account details, updates, password changes, and related operations.
abstract class AccountServiceInterface {
  /// Retrieves the user's account details.
  Future<dynamic> getAccountDetails();
  
  /// Updates account information and permissions.
  ///
  /// [accountModel] contains the updated account data.
  Future<ApiResponse> updateAccountDetails(AccountModel accountModel);
  
  /// Deletes the user's account.
  Future<ApiResponse> deleteAccount();
  
  /// Changes the account password.
  ///
  /// [oldPassword] is the current password for verification.
  /// [newPassword] is the new password to set.
  Future<ApiResponse> changePassword(String oldPassword, String newPassword);
  
  /// Activates a previously deactivated account.
  Future<ApiResponse> activateAccount();
  
  /// Requests a new bank card verification code.
  Future<ApiResponse> resendBankCardCode();
  
  /// Retrieves the user's recent transactions.
  Future<ApiResponse> getRecentTransactions();
}