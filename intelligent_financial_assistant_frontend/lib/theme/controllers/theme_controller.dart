import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Manages the application's theme mode (light/dark).
///
/// This controller handles theme toggling and persistence using SharedPreferences.
/// The theme preference is loaded on initialization and saved whenever changed.
class ThemeController with ChangeNotifier {
  /// SharedPreferences instance for storing theme preference.
  final SharedPreferences sharedPreferences;

  /// Creates a [ThemeController] and loads the saved theme preference.
  ThemeController({required this.sharedPreferences}) {
    _loadThemeMode();
  }

  bool _isDarkMode = false;
  
  /// Returns true if dark mode is currently enabled.
  bool get isDarkMode => _isDarkMode;

  /// Toggles between light and dark theme modes.
  ///
  /// Automatically saves the new preference and notifies listeners.
  void toggleThemeMode() {
    _isDarkMode = !_isDarkMode;
    saveThemeMode(_isDarkMode);
    notifyListeners();
 }

  /// Loads the saved theme mode from SharedPreferences.
  void _loadThemeMode() {
    _isDarkMode = sharedPreferences.getBool(AppConstants.theme) ?? false;
    notifyListeners();
  }

  /// Saves the theme mode preference to SharedPreferences.
  ///
  /// [isDarkMode] indicates whether dark mode should be enabled.
  void saveThemeMode(bool isDarkMode) async {
    sharedPreferences.setBool(AppConstants.theme, isDarkMode);
  }
}