import 'package:intelligent_financial_assistant_frontend/localization/models/language_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/images.dart';
class AppConstants {

  static List<LanguageModel> languages = [
    LanguageModel(imageUrl: Images.fr, languageName: 'French', countryCode: 'FR', languageCode: 'fr'),
    LanguageModel(imageUrl: Images.ar, languageName: 'Arabic', countryCode: 'MA', languageCode: 'ar'),
    LanguageModel(imageUrl: Images.en, languageName: 'English', countryCode: 'US', languageCode: 'en'),
  ];
}