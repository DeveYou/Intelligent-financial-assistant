import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/login_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/register_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service.dart';

class AuthenticationController with ChangeNotifier{
  final AuthenticationService authenticationService;
  AuthenticationController({required this.authenticationService});

  bool _isAuthenticated = false;
  bool _isLoading = false;
  int _selectedIndex = 0;

  bool get isLoading => _isLoading;
  bool get isAuthenticated => _isAuthenticated;
  int get selectedIndex => _selectedIndex;


  updateAuthenticationStatus() {
    _isAuthenticated = !_isAuthenticated;
    notifyListeners();
  }

  updateSelectedIndex(int index, {bool notify = true}) {
    _selectedIndex = index;
    if (notify) notifyListeners();
  }

  Future<bool> clearSharedData()  {
    return authenticationService.clearSharedData();
  }

  Future<void> updateToken(BuildContext context) async {
    ApiResponse apiResponse = await authenticationService.updateToken();
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
    } else {
      ApiChecker.checkApi(apiResponse);
    }
  }

  String getUserToken() {
    return authenticationService.getUserToken();
  }
  String getUserEmail() {
    return authenticationService.getUserEmail();
  }
  String getUserPassword() {
    return authenticationService.getUserPassword();
  }

  Future<void> registerUser(RegisterModel register, Function callback) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await authenticationService.registerUser(register.toJson());
    _isLoading = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      Map map = apiResponse.response!.data;
      String? temporaryToken = '', token = '', message = '';
      try{
        message = map["message"];
        token = map["token"];
        temporaryToken = map["temporary_token"];
      }catch(e){
        message = null;
        token = null;
        temporaryToken = null;
      }
      if(token != null && token.isNotEmpty){
        authenticationService.saveUserToken(token);
        await authenticationService.updateToken();
      }
      if(map['id'] != null){
        authenticationService.saveUserId(map['id'].toString());
      }
      callback(true, token, temporaryToken, message);
      notifyListeners();
    }else{
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  Future<void> signIn(LoginModel loginBody, Function callback) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await authenticationService.signIn(loginBody.toJson());
    _isLoading = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      Map map = apiResponse.response!.data;
      String? temporaryToken = '', token = '', message = '';
      try{
        message = map["message"];
        token = map["token"];
        temporaryToken = map["temporary_token"];
      }catch(e){
        message = null;
        token = null;
        temporaryToken = null;
      }
      if(token != null && token.isNotEmpty){
        authenticationService.saveUserToken(token);
        await authenticationService.updateToken();
      }
      if(map['id'] != null){
        authenticationService.saveUserId(map['id'].toString());
      }
      callback(true, token, temporaryToken, message);
      notifyListeners();
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  Future<void> signOut() async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await authenticationService.signOut();
    _isLoading = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      await clearSharedData();
      _isAuthenticated = false;
      notifyListeners();
    } else {
      ApiChecker.checkApi(apiResponse);
    }
    notifyListeners();
  }

  Future<void> saveUserCredentials(String email, String password) async {
    await authenticationService.saveUserCredentials(email, password);
  }

  Future<void> clearUserEmailAndPassword() async {
    await authenticationService.clearUserEmailAndPassword();
  }
}