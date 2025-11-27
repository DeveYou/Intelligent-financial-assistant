import 'package:intelligent_financial_assistant_frontend/localization/models/language_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/images.dart';
class AppConstants {

  static const String appName = "Intelligent Financial Assistant";
  static const double appVersion = 1.0;

  static const String baseUrl = "http://localhost:8000";


  // Application languages
  static List<LanguageModel> languages = [
    LanguageModel(imageUrl: Images.fr, languageName: 'French', countryCode: 'FR', languageCode: 'fr'),
    LanguageModel(imageUrl: Images.ar, languageName: 'Arabic', countryCode: 'MA', languageCode: 'ar'),
    LanguageModel(imageUrl: Images.en, languageName: 'English', countryCode: 'US', languageCode: 'en'),
  ];


  // Shared preferences keys
  static const String theme = "theme";
  static const String token = "token";
  static const String countryCode = 'country_code';
  static const String languageCode = "language_code";
  static const String langKey = 'lang';
  static const String localizationKey = 'X-localization';
  static const String userEmail = "user_email";
  static const String userPassword = "user_password";
  static const String notificationSound = "sound";
  static const String notificationCount = "count";
  static const String userLoginToken = 'user_login_token';
  static const String topic = 'financial_assistant';
  static const String demoTopic = 'demo_reset';


  // API Endpoints
  static const String loginUri = "/api/v1/auth/login";
  static const String registerUri = "/api/v1/auth/register";
  static const String userInfoUri = "/api/v1/auth/user-info";
  static const String logoutUri = "/api/v1/auth/logout";
  static const String forgotPasswordUri = "/api/v1/auth/forgot-password";
  static const String resetPasswordUri = "/api/v1/auth/reset-password";
  static const String updateTokenUri = "/api/v1/auth/update-token";


}