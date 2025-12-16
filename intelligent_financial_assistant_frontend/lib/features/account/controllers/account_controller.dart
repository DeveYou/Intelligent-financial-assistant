import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';

enum AccountState { loading, success, error }

class AccountController with ChangeNotifier {
  final AccountServiceInterface accountService;

  AccountController({required this.accountService});

  AccountState _accountState = AccountState.loading;
  AccountModel? _accountModel;
  List<TransactionModel>? _recentTransactions;
  String _error = '';

  AccountState get accountState => _accountState;
  bool get isLoading => _accountState == AccountState.loading;
  AccountModel? get accountModel => _accountModel;
  List<TransactionModel>? get recentTransactions => _recentTransactions;
  String get error => _error;

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

  Future<void> _getRecentTransactions() async {
    ApiResponse response = await accountService.getRecentTransactions();
    if (response.response != null && response.response!.statusCode == 200) {
      List<dynamic> list = response.response!.data;
      _recentTransactions = list.map((e) => TransactionModel.fromJson(e)).toList();
    }
  }

  // Method to toggle specific settings
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