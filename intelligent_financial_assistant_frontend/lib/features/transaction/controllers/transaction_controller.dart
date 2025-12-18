import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:dio/dio.dart' as import_dio;
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/services/transaction_service_interface.dart';

class TransactionController with ChangeNotifier {
  final TransactionServiceInterface transactionService;

  TransactionController({required this.transactionService});

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  List<TransactionModel> _transactions = [];
  List<TransactionModel> get transactions => _transactions;

  TransactionModel? _selectedTransaction;
  TransactionModel? get selectedTransaction => _selectedTransaction;

  double _accountBalance = 0.0;
  double get accountBalance => _accountBalance;

  set accountBalance(double value) => _accountBalance = value;

  TransactionModel? _lastTransaction;
  TransactionModel? get lastTransaction => _lastTransaction;

  String _getSafeText(String key, String fallback) {
    if (Get.context != null) {
      return getTranslated(key, Get.context!) ?? fallback;
    }
    return fallback;
  }

  void _showSnackBar(String message, Color color) {
    final context = Get.context;
    if (context == null) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: color),
    );
  }

  Future<void> getTransactions({int limit = 20}) async {
    _isLoading = true;
    notifyListeners();

    ApiResponse apiResponse = await transactionService.getTransactions(limit: limit);
    _isLoading = false;

    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _transactions = [];
      apiResponse.response!.data.forEach((transaction) {
        _transactions.add(TransactionModel.fromJson(transaction));
      });
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  Future<void> getTransactionDetails(String transactionId) async {
    _isLoading = true;
    notifyListeners();

    ApiResponse apiResponse = await transactionService.getTransactionDetails(transactionId);
    _isLoading = false;

    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _selectedTransaction = TransactionModel.fromJson(apiResponse.response!.data);
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  Future<void> getAccountBalance() async {
    _isLoading = true;
    notifyListeners();

    ApiResponse apiResponse = await transactionService.getAccountBalance();
    _isLoading = false;

    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _accountBalance = apiResponse.response!.data['balance'];
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  Future<bool> sendMoney(TransactionModel transaction) async {
    _isLoading = true;
    notifyListeners();

    final amount = transaction.amount ?? 0.0;

    // Proactive Validation
    if(amount > accountBalance) {
       _isLoading = false;
       notifyListeners();
       _showSnackBar(_getSafeText("insufficient_balance", "Insufficient Balance") , Colors.red);
       return false;
    }

    try {
      ApiResponse apiResponse = await transactionService.sendMoney(transaction);
      _isLoading = false;

      if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
        await getAccountBalance(); // Refresh balance after sending money
        await getTransactions(); // Refresh transaction list
        return true;
      } else {
        ApiChecker.checkApi(apiResponse);
        return false;
      }
    } on import_dio.DioException catch (e) {
      _isLoading = false;
      notifyListeners();
      if (e.type == import_dio.DioExceptionType.connectionTimeout || 
          e.type == import_dio.DioExceptionType.sendTimeout ||
          e.type == import_dio.DioExceptionType.receiveTimeout) {
        _showSnackBar(_getSafeText("request_timed_out_check_history", "Request timed out"), Colors.orange);
         return false;
      }
      ApiChecker.checkApi(ApiResponse.withError(ApiErrorHandler.getMessage(e)));
      return false;
    } catch (e) {
       _isLoading = false;
       notifyListeners();
       ApiChecker.checkApi(ApiResponse.withError(ApiErrorHandler.getMessage(e)));
       return false;
    }
  }

  Future<void> getLastTransaction() async {
    _isLoading = true;
    notifyListeners();

    ApiResponse apiResponse = await transactionService.getLastTransaction();
    _isLoading = false;

    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _lastTransaction = TransactionModel.fromJson(apiResponse.response!.data);
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }
}