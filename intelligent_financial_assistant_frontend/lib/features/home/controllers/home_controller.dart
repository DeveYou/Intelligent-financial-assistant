import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/models/home_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/services/home_service.dart';

/// Represents the loading state of home screen data.
enum HomeState { 
  /// Data is being loaded.
  loading, 
  
  /// Data was successfully loaded.
  success, 
  
  /// An error occurred during loading.
  error 
}

/// Manages home screen data and account summary.
///
/// This controller fetches and displays account information on the home screen.
class HomeController with ChangeNotifier {
  /// The service for home screen operations.
  final HomeService homeService;

  /// Creates a [HomeController] with the required service.
  HomeController({required this.homeService});

  HomeModel? _accountDetails;
  HomeState _homeState = HomeState.loading;
  String _errorMessage = '';

  /// Gets the account details/summary.
  HomeModel? get accountDetails => _accountDetails;
  
  /// Gets the current state of the home screen.
  HomeState get homeState => _homeState;
  
  /// Gets the error message if an error occurred.
  String get errorMessage => _errorMessage;

  /// Fetches the account summary for the home screen.
  ///
  /// Displays account holder name, IBAN, balance, and currency.
  Future<void> getAccountSummary() async {
    _homeState = HomeState.loading;
    _errorMessage = '';
    notifyListeners();

    try {
      final ApiResponse apiResponse = await homeService.getAccountSummary();
      if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
        var data = apiResponse.response!.data;
        if (data is List) {
          if (data.isNotEmpty) {
            var acc = data[0];
            _accountDetails = HomeModel(
              accountHolder: 'User ${acc['userId']}',
              accountNumber: acc['iban'] ?? '****',
              bankName: 'VoxBank',
              balance: double.tryParse(acc['balance'].toString()) ?? 0.0,
              currency: 'MAD',
            );
          } else {
            _accountDetails = null;
          }
           _homeState = HomeState.success;
        } else {
           _accountDetails = HomeModel.fromJson(data);
           _homeState = HomeState.success;
        }
      } else {
        ApiChecker.checkApi(apiResponse);
        _errorMessage = 'Failed to load account summary';
        _accountDetails = null;
        _homeState = HomeState.error;
      }
    } catch (e) {
      _errorMessage = 'An error occurred: $e';
      _accountDetails = null;
      _homeState = HomeState.error;
    } finally {
      notifyListeners();
    }
  }
}