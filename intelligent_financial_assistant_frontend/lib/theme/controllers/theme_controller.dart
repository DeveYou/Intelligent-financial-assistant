import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ThemeController with ChangeNotifier {
  final SharedPreferences sharedPreferences;

  ThemeController({required this.sharedPreferences}) {
    _loadThemeMode();
  }

  bool _isDarkMode = false;
  bool get isDarkMode => _isDarkMode;

  void toggleThemeMode() {
    _isDarkMode = !_isDarkMode;
    saveThemeMode(_isDarkMode);
    notifyListeners();
  }

  void _loadThemeMode() {
    _isDarkMode = sharedPreferences.getBool(AppConstants.theme) ?? false;
    notifyListeners();
  }

  void saveThemeMode(bool isDarkMode) async {
    sharedPreferences.setBool(AppConstants.theme, isDarkMode);
  }
}