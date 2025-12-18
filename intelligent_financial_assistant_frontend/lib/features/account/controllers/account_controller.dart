import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';

/// Represents the loading state of account operations.
enum AccountState { 
  /// Data is being loaded from the API.
  loading, 
  
  /// Data has been successfully loaded.
  success, 
  
  /// An error occurred during the operation.
  error 
}

/// Manages account data and settings.
///
/// This controller handles account information retrieval, recent transactions,
/// and account permission toggles (card payments, withdrawals, etc.).
/// Uses optimistic updates for better UX with automatic rollback on failure.
class AccountController with ChangeNotifier {
  /// The service interface for account operations.
  final AccountServiceInterface accountService;

  /// Creates an [AccountController] with the required service.
  AccountController({required this.accountService});

  AccountState _accountState = AccountState.loading;
  AccountModel? _accountModel;
  List<TransactionModel>? _recentTransactions;
  String _error = '';

  /// Gets the current account state.
  AccountState get accountState => _accountState;
  
  /// Returns true if data is currently being loaded.
  bool get isLoading => _accountState == AccountState.loading;
  
  /// Gets the account model data.
  AccountModel? get accountModel => _accountModel;
  
  /// Gets the list of recent transactions.
  List<TransactionModel>? get recentTransactions => _recentTransactions;
  
  /// Gets the error message if an error occurred.
  String get error => _error;

  /// Initializes account data by fetching account details and recent transactions concurrently.
  ///
  /// This method loads both account information and recent transactions in parallel
  /// for better performance. Updates the state to success or error based on the results.
  Future<void> initAccountData() async {
    _accountState = AccountState.loading;
    _error = '';
    notifyListeners();

    try {
      await Future.wait([
        _getAccountDetails(),
        _getRecentTransactions(),
      ]);
      if (_error.isNotEmpty) {
        _accountState = AccountState.error;
      } else {
        _accountState = AccountState.success;
      }
    } catch (e) {
      _error = 'Unexpected error: $e';
      _accountState = AccountState.error;
    } finally {
      notifyListeners();
    }
  }

  /// Fetches account details from the API.
  ///
  /// Handles both single object and array responses from the backend.
  Future<void> _getAccountDetails() async {
    ApiResponse response = await accountService.getAccountDetails();
    if (response.response != null && response.response!.statusCode == 200) {
      var data = response.response!.data;
      if (data is List) {
        if(data.isNotEmpty) {
           _accountModel = AccountModel.fromJson(data[0]);
        } else {
           _accountModel = null; // Handle no account case
        }
      } else {
        _accountModel = AccountModel.fromJson(data);
      }
    } else {
      _error = response.error.toString();
    }
  }

  /// Fetches recent transactions for the account.
  Future<void> _getRecentTransactions() async {
    ApiResponse response = await accountService.getRecentTransactions();
    if (response.response != null && response.response!.statusCode == 200) {
      List<dynamic> list = response.response!.data;
      _recentTransactions = list.map((e) => TransactionModel.fromJson(e)).toList();
    }
  }

  /// Toggles a specific account setting/permission.
  ///
  /// Uses optimistic update pattern: updates UI immediately, then syncs with server.
  /// Automatically rolls back the change if the API request fails.
  ///
  /// Supported setting types:
  /// - 'activate_account': Enable/disable the account
  /// - 'card_payment': Enable/disable card payments
  /// - 'withdrawal': Enable/disable ATM withdrawals
  /// - 'online_payment': Enable/disable online payments
  /// - 'contactless': Enable/disable contactless payments
  ///
  /// [settingType] specifies which setting to toggle.
  /// [value] is the new value for the setting.
  /// [context] is used to show success/error snackbars.
  Future<void> toggleSetting(String settingType, bool value, BuildContext context) async {
    if (_accountModel == null) return;

    // Optimistic Update
    AccountModel previousModel = _accountModel!;
    AccountModel updatedModel;

    switch (settingType) {
      case 'activate_account':
        updatedModel = _accountModel!.copyWith(isActive: value);
        break;
      case 'card_payment':
        updatedModel = _accountModel!.copyWith(isPaymentByCard: value);
        break;
      case 'withdrawal':
        updatedModel = _accountModel!.copyWith(isWithdrawal: value);
        break;
      case 'online_payment':
        updatedModel = _accountModel!.copyWith(isOnlinePayment: value);
        break;
      case 'contactless':
        updatedModel = _accountModel!.copyWith(isContactless: value);
        break;
      default:
        return;
    }

    _accountModel = updatedModel;
    notifyListeners();

    // API Call
    ApiResponse response = await accountService.updateAccountDetails(updatedModel);

    // Rollback if failed
    if (response.response == null || response.response!.statusCode != 200) {
      _accountModel = previousModel;
      notifyListeners();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response.error.toString()), backgroundColor: Colors.red),
      );
    }
  }

  /// Requests a new verification code to be sent to the user's bank card.
  ///
  /// Shows a success or error snackbar based on the result.
  Future<void> resendBankCardCode(BuildContext context) async {
    ApiResponse response = await accountService.resendBankCardCode();
    if (response.response != null && response.response!.statusCode == 200) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(getTranslated('code_sent_successfully', Get.context!)!), backgroundColor: Colors.green),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response.error.toString()), backgroundColor: Colors.red),
      );
    }
  }
}