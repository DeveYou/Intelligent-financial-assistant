import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AppStorageService {

  /// Check if it is the first time the user is opening the app
  static Future<bool> isFirstTime() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(AppConstants.kIsFirstTime) ?? true;
  }

  /// Mark onboarding as complete (call this when user finishes onboarding)
  static Future<void> completeOnboarding() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(AppConstants.kIsFirstTime, false);
  }

  /// Check if user is currently logged in
  static Future<bool> isLoggedIn() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(AppConstants.kIsLoggedIn) ?? false;
  }

  /// Set login status (call this after successful login/signup)
  static Future<void> setLoggedIn(bool status) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(AppConstants.kIsLoggedIn, status);
  }
}