/// Service interface for authentication operations.
///
/// Defines the contract for user authentication, registration, token management,
/// and credential storage. Handles all authentication-related business logic.
abstract class AuthenticationServiceInterface {
  /// Registers a new user account.
  ///
  /// [data] contains user registration information (email, password, name, etc.).
  Future<dynamic> registerUser(Map<String, dynamic> data);
  
  /// Signs in an existing user.
  ///
  /// [data] contains login credentials (email, password).
  Future<dynamic> signIn(Map<String, dynamic> data);
  
  /// Signs out the current user and clears authentication data.
  Future<dynamic> signOut();
  
  /// Checks if a user is currently signed in.
  bool isSignedIn();
  
  /// Clears all stored authentication data from local storage.
  Future<bool> clearSharedData();
  
  /// Retrieves the stored authentication token.
  String getUserToken();
  
  /// Retrieves the stored user email.
  String getUserEmail();
  
  /// Retrieves the stored user password.
  String getUserPassword();
  
  /// Saves user credentials for "Remember Me" functionality.
  ///
  /// [email] is the user's email address.
  /// [password] is the user's password.
  Future<void> saveUserCredentials(String email, String password);
  
  /// Clears saved email and password from local storage.
  Future<bool> clearUserEmailAndPassword();
  
  /// Updates the authentication token with the backend.
  Future<dynamic> updateToken();
  
  /// Saves the authentication token to local storage.
  ///
  /// [token] is the JWT or auth token to store.
  Future<void> saveUserToken(String token);
  
  /// Saves the user ID to local storage.
  ///
  /// [id] is the user's unique identifier.
  Future<void> saveUserId(String id);
  
  /// Retrieves the stored user ID.
  String getUserId();
}