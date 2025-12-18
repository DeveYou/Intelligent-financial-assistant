import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:dio/dio.dart' as import_dio;
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/models/transaction_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/domains/services/transaction_service_interface.dart';

/// Manages transaction data and operations.
///
/// This controller handles all transaction-related functionality including
/// fetching transactions, sending money, retrieving balances, and managing
/// transaction details. It uses [ChangeNotifier] for state management.
class TransactionController with ChangeNotifier {
  /// The service interface for transaction operations.
  final TransactionServiceInterface transactionService;

  /// Creates a [TransactionController] with the required service.
  TransactionController({required this.transactionService});

  bool _isLoading = false;
  
  /// Returns true if a transaction operation is in progress.
  bool get isLoading => _isLoading;

  List<TransactionModel> _transactions = [];
  
  /// Gets the list of user transactions.
  List<TransactionModel> get transactions => _transactions;

  TransactionModel? _selectedTransaction;
  
  /// Gets the currently selected transaction details.
  TransactionModel? get selectedTransaction => _selectedTransaction;

  double _accountBalance = 0.0;
  
  /// Gets the current account balance.
  double get accountBalance => _accountBalance;

  /// Sets the account balance.
  set accountBalance(double value) => _accountBalance = value;

  TransactionModel? _lastTransaction;
  
  /// Gets the most recent transaction.
  TransactionModel? get lastTransaction => _lastTransaction;

  /// Safely retrieves a localized text string with a fallback.
  ///
  /// Returns the translated string for [key] if context is available,
  /// otherwise returns [fallback].
  String _getSafeText(String key, String fallback) {
    if (Get.context != null) {
      return getTranslated(key, Get.context!) ?? fallback;
    }
    return fallback;
  }

  /// Shows a snackbar message with the specified color.
  void _showSnackBar(String message, Color color) {
    final BuildContext? context = Get.context;
    if (context == null) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: color),
    );
  }

  /// Retrieves the user's transaction history from the API.
  ///
  /// [limit] specifies the maximum number of transactions to fetch (default: 20).
  Future<void> getTransactions({int limit = 20}) async {
    _isLoading = true;
    notifyListeners();

    ApiResponse apiResponse = await transactionService.getTransactions(limit: limit);
    _isLoading = false;

    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _transactions = [];
      apiResponse.response!.data.forEach((dynamic transaction) {
        _transactions.add(TransactionModel.fromJson(transaction));
      });
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  /// Fetches detailed information for a specific transaction.
  ///
  /// [transactionId] is the unique identifier of the transaction to retrieve.
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

  /// Retrieves the current account balance from the API.
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

  /// Initiates a money transfer transaction.
  ///
  /// Validates the transaction amount against the current balance before sending.
  /// Automatically refreshes the balance and transaction list on success.
  /// Handles timeout errors with appropriate user feedback.
  ///
  /// [transaction] contains the transfer details (amount, recipient, etc.).
  ///
  /// Returns true if the transaction was successful, false otherwise.
  Future<bool> sendMoney(TransactionModel transaction) async {
    _isLoading = true;
    notifyListeners();

    final double amount = transaction.amount ?? 0.0;

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

  /// Retrieves the user's most recent transaction.
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