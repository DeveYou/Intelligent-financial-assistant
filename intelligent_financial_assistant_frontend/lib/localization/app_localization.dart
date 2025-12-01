
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get_it/get_it.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class AppLocalization {
  final Locale locale;
  AppLocalization(this.locale);

  static AppLocalization? of(BuildContext context) {
    return Localizations.of<AppLocalization>(context, AppLocalization)!;
  }

  late Map<String, String> _localizedStrings;

  Future<void> load() async {
    try {
      String jsonString = await rootBundle.loadString('assets/languages/${locale.languageCode}.json');
      Map<String, dynamic> jsonMap = json.decode(jsonString);
      _localizedStrings = jsonMap.map((key, value) => MapEntry(key, value.toString()));
    } catch (e) {
      debugPrint('Error loading language file: $e');
      _localizedStrings = {};
    }
  }

  String? translate(String? key) {
    throwIf(_localizedStrings[key] == null, 'Key $key not found in localization files');
    return _localizedStrings[key!];
  }

  static const LocalizationsDelegate<AppLocalization> delegate = _AppLocalizationDelegate();

}

class _AppLocalizationDelegate extends LocalizationsDelegate<AppLocalization> {
  const _AppLocalizationDelegate();

  @override
  bool isSupported(Locale locale) {
    List<String?> languages = [];
    for (var lang in AppConstants.languages) {
      languages.add(lang.languageCode);
    }
    return languages.contains(locale.languageCode);
  }

  @override
  Future<AppLocalization> load(Locale locale) async {
    AppLocalization localization = AppLocalization(locale);
    await localization.load();
    return localization;
  }

  @override
  bool shouldReload(covariant LocalizationsDelegate<AppLocalization> old) => false;
}