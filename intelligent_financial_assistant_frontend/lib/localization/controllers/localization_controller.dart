import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LocalizationController extends ChangeNotifier{
  final SharedPreferences? sharedPreferences;
  final DioClient? dioClient;
  LocalizationController({required this.sharedPreferences, required this.dioClient}) {
    _loadCurrentLanguage();
  }

  Locale _locale = Locale(AppConstants.languages[0].languageCode!, AppConstants.languages[0].countryCode);
  bool _isLtr = true;
  int? _languageIndex;

  Locale get locale => _locale;
  bool get isLtr => _isLtr;
  int? get languageIndex => _languageIndex;

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

  _saveLanguage(Locale locale) async {
    sharedPreferences!.setString(AppConstants.languageCode, locale.languageCode);
    sharedPreferences!.setString(AppConstants.countryCode, locale.countryCode!);
  }

  String? getCurrentLanguage() {
    return sharedPreferences!.getString(AppConstants.countryCode) ?? "US";
  }
}