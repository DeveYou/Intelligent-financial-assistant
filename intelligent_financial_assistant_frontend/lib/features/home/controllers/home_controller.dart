import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/models/home_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/domains/services/home_service.dart';

class HomeController with ChangeNotifier {
  final HomeService homeService;

  HomeController({required this.homeService});

  HomeModel? _accountDetails;
  bool _isLoading = false;
  String _errorMessage = '';

  HomeModel? get accountDetails => _accountDetails;
  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;


  Future<void> getAccountSummary() async {
    _isLoading = true;
    _errorMessage = '';
    notifyListeners();

    try {
      final ApiResponse apiResponse = await homeService.getAccountSummary();
      if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
        _accountDetails = HomeModel.fromJson(apiResponse.response!.data);
      } else {
        ApiChecker.checkApi(apiResponse);
        _errorMessage = 'Failed to load account summary';
        _accountDetails = null;
      }
    } catch (e) {
      _errorMessage = 'An error occurred: $e';
      _accountDetails = null;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}