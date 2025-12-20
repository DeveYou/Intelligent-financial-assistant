import 'dart:io';

import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/repositories/authentication_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthenticationRepository implements AuthenticationRepositoryInterface{
  final DioClient? dioClient;
  final SharedPreferences? sharedPreferences;
  AuthenticationRepository({required this.dioClient, required this.sharedPreferences});

  @override
  Future<bool> clearUserEmailAndPassword() async {
    await sharedPreferences!.remove(AppConstants.userPassword);
    return await sharedPreferences!.remove(AppConstants.userEmail);
  }

  @override
  String getUserEmail() {
    return sharedPreferences!.getString(AppConstants.userEmail) ?? "";
  }

  @override
  String getUserPassword() {
    return sharedPreferences!.getString(AppConstants.userPassword) ?? "";
  }

  @override
  String getUserToken() {
    return sharedPreferences!.getString(AppConstants.userLoginToken) ?? "";
  }

  @override
  bool isSignedIn() {
    return sharedPreferences!.containsKey(AppConstants.userLoginToken);
  }

  @override
  Future<void> saveUserCredentials(String email, String password) async {
    try {
      await sharedPreferences!.setString(AppConstants.userEmail, email);
      await sharedPreferences!.setString(AppConstants.userPassword, password);
    } catch (e) {
      rethrow;
    }
  }


  @override
  Future<ApiResponse> registerUser(Map<String, dynamic> data) async {
    try {
      Response response = await dioClient!.post(AppConstants.registerUri, data: data);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }



  @override
  Future<ApiResponse> signIn(Map<String, dynamic> data) async{
    try {
      Response response = await dioClient!.post(AppConstants.loginUri, data: data);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> signOut() async {
    try {
      Response response = await dioClient!.post(AppConstants.logoutUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<void> saveUserToken(String token) async {
    dioClient!.updateHeader(token, null);
    try {
      await sharedPreferences!.setString(AppConstants.userLoginToken, token);
    } catch (e) {
      rethrow;
    }
  }

  @override
  Future<void> saveUserId(String id) async {
    try {
      await sharedPreferences!.setString(AppConstants.userId, id);
    } catch (e) {
      rethrow;
    }
  }

  @override
  String getUserId() {
    return sharedPreferences!.getString(AppConstants.userId) ?? "";
  }

  @override
  Future<bool> clearSharedData() async {
    sharedPreferences?.remove(AppConstants.userLoginToken);
    sharedPreferences?.remove(AppConstants.userId);
    return true;
  }


  Future<String?> _getToken() async {
    String? deviceToken;
    if(Platform.isIOS) {
      deviceToken = await FirebaseMessaging.instance.getAPNSToken();
    }else {
      deviceToken = await FirebaseMessaging.instance.getToken();
    }
    if (deviceToken != null) {

    }
    return deviceToken;
  }
}