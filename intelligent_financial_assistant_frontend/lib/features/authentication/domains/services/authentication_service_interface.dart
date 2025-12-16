abstract class AuthenticationServiceInterface {
  Future<dynamic> registerUser(Map<String, dynamic> data);
  Future<dynamic> signIn(Map<String, dynamic> data);
  Future<dynamic> signOut();
  bool isSignedIn();
  Future<bool> clearSharedData();
  String getUserToken();
  String getUserEmail();
  String getUserPassword();
  Future<void> saveUserCredentials(String email, String password);
  Future<bool> clearUserEmailAndPassword();
  Future<dynamic> updateToken();
  Future<void> saveUserToken(String token);
  Future<void> saveUserId(String id);
  String getUserId();
}