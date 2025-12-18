import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Manages the application's localization and language preferences.
///
/// This controller handles language selection, persistence, and RTL/LTR text direction.
/// It automatically updates the DioClient headers when the language changes.
class LocalizationController extends ChangeNotifier{
  /// SharedPreferences instance for storing language preference.
  final SharedPreferences? sharedPreferences;
  
  /// DioClient instance for updating HTTP request headers with language info.
  final DioClient? dioClient;
  
  /// Creates a [LocalizationController] and loads the saved language preference.
  LocalizationController({required this.sharedPreferences, required this.dioClient}) {
    _loadCurrentLanguage();
  }

  Locale _locale = Locale(AppConstants.languages[0].languageCode!, AppConstants.languages[0].countryCode);
  bool _isLtr = true;
  int? _languageIndex;

  /// Gets the current locale.
  Locale get locale => _locale;
  
  /// Returns true if the current language uses left-to-right text direction.
  ///
  /// Returns false for right-to-left languages (e.g., Arabic).
  bool get isLtr => _isLtr;
  
  /// Gets the index of the current language in the AppConstants.languages list.
  int? get languageIndex => _languageIndex;

  /// Sets the application language to the specified locale.
  ///
  /// Updates the text direction, saves the preference, and updates API headers.
  ///
  /// [locale] is the new locale to use (language and country code).
  void setLanguage(Locale locale) {
    _locale = locale;
    _isLtr = _locale.languageCode != 'ar';
    dioClient!.updateHeader(null, locale.countryCode);

    for(int index=0; index<AppConstants.languages.length; index++) {
      if(AppConstants.languages[index].languageCode == locale.languageCode) {
        _languageIndex = index;
        break;
      }
    }
    _saveLanguage(_locale);
    notifyListeners();
  }

  /// Loads the saved language preference from SharedPreferences.
  _loadCurrentLanguage() async {
    _locale = Locale(sharedPreferences!.getString(AppConstants.languageCode) ?? AppConstants.languages[0].languageCode!,
        sharedPreferences!.getString(AppConstants.countryCode) ?? AppConstants.languages[0].countryCode);
    _isLtr = _locale.languageCode != 'ar';
    for(int index=0; index<AppConstants.languages.length; index++) {
      if(AppConstants.languages[index].languageCode == locale.languageCode) {
        _languageIndex = index;
        break;
      }
    }
    notifyListeners();
  }

  /// Saves the language preference to SharedPreferences.
  _saveLanguage(Locale locale) async {
    sharedPreferences!.setString(AppConstants.languageCode, locale.languageCode);
    sharedPreferences!.setString(AppConstants.countryCode, locale.countryCode!);
  }

  /// Gets the current country code (e.g., "US", "FR").
  ///
  /// Returns "US" as the default if no language is set.
  String? getCurrentLanguage() {
    return sharedPreferences!.getString(AppConstants.countryCode) ?? "US";
  }
}