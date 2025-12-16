import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/repositories/authentication_repository.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/domains/services/authentication_service_interface.dart';

class AuthenticationService implements AuthenticationServiceInterface{
  AuthenticationRepository authenticationRepository;

  AuthenticationService({required this.authenticationRepository});

  @override
  Future<bool> clearSharedData() {
    return authenticationRepository.clearSharedData();
  }

  @override
  Future<bool> clearUserEmailAndPassword() {
    return authenticationRepository.clearUserEmailAndPassword();
  }

  @override
  String getUserEmail() {
    return authenticationRepository.getUserEmail();
  }

  @override
  String getUserPassword() {
    return authenticationRepository.getUserPassword();
  }

  @override
  String getUserToken() {
    return authenticationRepository.getUserToken();
  }

  @override
  bool isSignedIn() {
    return authenticationRepository.isSignedIn();
  }

  @override
  Future<void> saveUserCredentials(String email, String password) {
    return authenticationRepository.saveUserCredentials(email, password);
  }

  @override
  Future<dynamic> signIn(Map<String, dynamic> data) {
    return authenticationRepository.signIn(data);
  }

  @override
  Future<dynamic> signOut() {
    return authenticationRepository.signOut();
  }

  @override
  Future<dynamic> updateToken() {
    return authenticationRepository.updateToken();
  }

  @override
  Future<void> saveUserToken(String token) {
    return authenticationRepository.saveUserToken(token);
  }

  @override
  Future<void> saveUserId(String id) {
    return authenticationRepository.saveUserId(id);
  }

  @override
  String getUserId() {
    return authenticationRepository.getUserId();
  }

  @override
  Future<dynamic> registerUser(Map<String, dynamic> data) {
    return authenticationRepository.registerUser(data);
  }

}