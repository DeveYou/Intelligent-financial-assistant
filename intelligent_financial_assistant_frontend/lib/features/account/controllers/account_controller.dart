import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/models/account_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/account/domains/services/account_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';

class AccountController with ChangeNotifier {
  final AccountServiceInterface accountService;

  AccountController({required this.accountService});

  bool _isLoading = false;
  AccountModel? _accountModel;
  List<TransactionModel>? _recentTransactions;
  String _error = '';

  bool get isLoading => _isLoading;
  AccountModel? get accountModel => _accountModel;
  List<TransactionModel>? get recentTransactions => _recentTransactions;
  String get error => _error;

  Future<void> initAccountData() async {
    _isLoading = true;
    _error = '';
    notifyListeners();

    await Future.wait([
      _getAccountDetails(),
      _getRecentTransactions(),
    ]);

    _isLoading = false;
    notifyListeners();
  }

  Future<void> _getAccountDetails() async {
    ApiResponse response = await accountService.getAccountDetails();
    if (response.response != null && response.response!.statusCode == 200) {
      _accountModel = AccountModel.fromJson(response.response!.data);
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