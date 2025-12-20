import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Interface defining the contract for user authentication and session management.
abstract class AuthenticationRepositoryInterface{
  /// Registers a new user with the provided [data].
  Future<ApiResponse> registerUser(Map<String, dynamic> data);

  /// Authenticates a user using the provided [data] (typically email/phone and password).
  Future<ApiResponse> signIn(Map<String, dynamic> data);

  /// Signs the current user out of the application.
  Future<ApiResponse> signOut();

  /// Checks if a user is currently signed in.
  bool isSignedIn();

  /// Retrieves the current user's authentication token.
  String getUserToken();

  /// Retrieves the current user's email address.
  String getUserEmail();

  /// Retrieves the current user's password (if stored locally).
  String getUserPassword();

  /// Saves the user's [email] and [password] locally for auto-login/remember me features.
  Future<void> saveUserCredentials(String email, String password);

  /// Clears the stored user email and password.
  Future<bool> clearUserEmailAndPassword();



  /// Saves the new authentication [token] locally.
  Future<void> saveUserToken(String token);

  /// Clears all shared data related to the session.
  Future<bool> clearSharedData();

  /// Saves the user's unique [id] locally.
  Future<void> saveUserId(String id);

  /// Retrieves the current user's unique ID.
  String getUserId();
}