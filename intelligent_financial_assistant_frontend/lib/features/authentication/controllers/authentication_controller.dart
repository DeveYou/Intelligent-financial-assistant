import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/api/api_checker.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/login_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/register_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/models/register_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/repositories/notifications_repository_interface.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

/// Manages user authentication and session state.
///
/// This controller handles user registration, sign-in, sign-out, and token management.
/// It maintains the authentication status and provides access to user credentials.
class AuthenticationController with ChangeNotifier{
  /// The service for authentication operations.
  final AuthenticationService authenticationService;
  final NotificationsRepositoryInterface notificationRepository;
  
  /// Creates an [AuthenticationController] with the required service.
  AuthenticationController({required this.authenticationService, required this.notificationRepository});

  bool _isAuthenticated = false;
  bool _isLoading = false;
  int _selectedIndex = 0;

  /// Returns true if an authentication operation is in progress.
  bool get isLoading => _isLoading;
  
  /// Returns true if the user is currently authenticated.
  bool get isAuthenticated => _isAuthenticated;
  
  /// Gets the selected tab index (login vs register).
  int get selectedIndex => _selectedIndex;


  /// Toggles the authentication status.
  updateAuthenticationStatus() {
    _isAuthenticated = !_isAuthenticated;
    notifyListeners();
  }

  /// Updates the selected tab index.
  ///
  /// [index] is the new tab index (0 for login, 1 for register).
  /// [notify] determines whether to notify listeners (default: true).
  updateSelectedIndex(int index, {bool notify = true}) {
    _selectedIndex = index;
    if (notify) notifyListeners();
  }

  /// Clears all stored authentication data from SharedPreferences.
  ///
  /// Returns true if successful.
  Future<bool> clearSharedData()  {
    return authenticationService.clearSharedData();
  }

  /// Updates the authentication token with the backend.
  Future<void> updateToken(BuildContext context) async {
    ApiResponse apiResponse = await authenticationService.updateToken();
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
    } else {
      ApiChecker.checkApi(apiResponse);
    }
  }

  /// Retrieves the stored user authentication token.
  String getUserToken() {
    return authenticationService.getUserToken();
  }

  /// Retrieves the stored user ID.
  String getUserId() {
    return authenticationService.getUserId();
  }
  
  /// Retrieves the stored user email.
  String getUserEmail() {
    return authenticationService.getUserEmail();
  }
  
  /// Retrieves the stored user password.
  String getUserPassword() {
    return authenticationService.getUserPassword();
  }

  /// Registers a new user account.
  ///
  /// [register] contains the registration data (name, email, password).
  /// [callback] is called with (success, token, temporaryToken, message) upon completion.
  Future<void> registerUser(RegisterModel register, Function callback) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await authenticationService.registerUser(register.toJson());
    _isLoading = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      Map<String, dynamic> map = apiResponse.response!.data;
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
        String? fcmToken = await FirebaseMessaging.instance.getToken();
        if(fcmToken != null) {
           if(map['id'] != null){
             int? uId = int.tryParse(map['id'].toString());
             if(uId != null) {
                notificationRepository.registerToken(fcmToken, uId);
             }
           }
        }
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

  /// Signs in an existing user.
  ///
  /// [loginBody] contains the login credentials (email, password).
  /// [callback] is called with (success, token, temporaryToken, message) upon completion.
  Future<void> signIn(LoginModel loginBody, Function callback) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await authenticationService.signIn(loginBody.toJson());
    _isLoading = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      Map<String, dynamic> map = apiResponse.response!.data;
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
        String? fcmToken = await FirebaseMessaging.instance.getToken();
        if(fcmToken != null) {
           if(map['id'] != null){
             int? uId = int.tryParse(map['id'].toString());
             if(uId != null) {
                notificationRepository.registerToken(fcmToken, uId);
             }
           }
        }
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

  /// Signs out the current user and clears all stored data.
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

  /// Saves user credentials to SharedPreferences for "Remember Me" functionality.
  ///
  /// [email] is the user's email address.
  /// [password] is the user's password.
  Future<void> saveUserCredentials(String email, String password) async {
    await authenticationService.saveUserCredentials(email, password);
  }

  /// Clears saved user credentials from SharedPreferences.
  Future<void> clearUserEmailAndPassword() async {
    await authenticationService.clearUserEmailAndPassword();
  }
}