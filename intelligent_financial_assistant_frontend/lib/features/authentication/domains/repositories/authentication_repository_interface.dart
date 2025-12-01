import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

abstract class AuthenticationRepositoryInterface{
  Future<ApiResponse> registerUser(Map<String, dynamic> data);
  Future<ApiResponse> signIn(Map<String, dynamic> data);
  Future<ApiResponse> signOut();
  bool isSignedIn();
  String getUserToken();
  String getUserEmail();
  String getUserPassword();
  Future<void> saveUserCredentials(String email, String password);
  Future<bool> clearUserEmailAndPassword();
  Future<ApiResponse> updateToken();
  Future<void> saveUserToken(String token);
  Future<bool> clearSharedData();
}