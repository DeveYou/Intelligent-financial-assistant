import 'package:intelligent_financial_assistant_frontend/localization/models/language_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/images.dart';
class AppConstants {

  static const String appName = "Intelligent Financial Assistant";
  static const double appVersion = 1.0;

  static const String baseUrl = "http://localhost:8080";


  // Application languages
  static List<LanguageModel> languages = [
    LanguageModel(imageUrl: Images.fr, languageName: 'French', countryCode: 'FR', languageCode: 'fr'),
    LanguageModel(imageUrl: Images.ar, languageName: 'Arabic', countryCode: 'MA', languageCode: 'ar'),
    LanguageModel(imageUrl: Images.en, languageName: 'English', countryCode: 'US', languageCode: 'en'),
    LanguageModel(imageUrl: Images.es, languageName: 'Spanish', countryCode: 'ES', languageCode: 'es'),
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
  static const String kIsFirstTime = 'is_first_time';
  static const String kIsLoggedIn = 'is_logged_in';


  // API Endpoints
  static const String loginUri = "/api/v1/auth/login";
  static const String registerUri = "/api/v1/auth/register";
  static const String userInfoUri = "/api/v1/auth/user-info";
  static const String logoutUri = "/api/v1/auth/logout";
  static const String forgotPasswordUri = "/api/v1/auth/forgot-password";
  static const String resetPasswordUri = "/api/v1/auth/reset-password";
  static const String updateTokenUri = "/api/v1/auth/update-token";
  static const String homeUri = "/api/v1/customer/account/data";

  static const String accountUri = "/api/v1/customer/account/summary";
  static const String updateAccountUri = "/api/v1/customer/account/update";
  static const String resendCardCodeUri = "/api/v1/auth/resend-card-code";
  static const String deleteAccountUri = "/api/v1/customer/account/delete";

  static const String getTransactionsUri = "/api/v1/customer/transactions";

  static const String assistantMessageUri = "/api/v1/assistant/message";
  static const String getMessagesUri = "/api/v1/assistant/messages";


  static const String markNotificationAsReadUri = '/api/v1/notifications/mark-read';
  static const String sendNotificationUri = '/api/v1/notifications/send';
  static const String getNotificationsUri = '/api/v1/notifications';

  static const String addRecipientUri = '/api/v1/recipient/add';
  static const String updateRecipientUri = '/api/v1/recipients/update/';
  static const String deleteRecipientUri = '/api/v1/recipients/delete/';
  static const String getRecipientsUri = '/api/v1/recipients';

}