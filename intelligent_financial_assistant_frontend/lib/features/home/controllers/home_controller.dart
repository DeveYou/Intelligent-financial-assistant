import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/models/home_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/services/home_service.dart';

enum HomeState { loading, success, error }

class HomeController with ChangeNotifier {
  final HomeService homeService;

  HomeController({required this.homeService});

  HomeModel? _accountDetails;
  HomeState _homeState = HomeState.loading;
  String _errorMessage = '';

  HomeModel? get accountDetails => _accountDetails;
  HomeState get homeState => _homeState;
  String get errorMessage => _errorMessage;


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