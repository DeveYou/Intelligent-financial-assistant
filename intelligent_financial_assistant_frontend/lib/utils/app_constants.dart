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
  static const String loginUri = "/AUTH-SERVICE/auth/login";
  static const String registerUri = "/AUTH-SERVICE/auth/register";
  static const String userInfoUri = "/AUTH-SERVICE/auth/user-info";
  static const String logoutUri = "/AUTH-SERVICE/auth/logout";
  static const String forgotPasswordUri = "/AUTH-SERVICE/auth/forgot-password";
  static const String resetPasswordUri = "/AUTH-SERVICE/auth/reset-password";
  static const String updateTokenUri = "/AUTH-SERVICE/auth/update-token";

  static const String getAccountDataUri = "/ACCOUNT-SERVICE/api/accounts";

  static const String accountUri = "/ACCOUNT-SERVICE/api/accounts";
  static const String updateAccountUri = "/ACCOUNT-SERVICE/api/accounts";
  static const String resendCardCodeUri = "/ACCOUNT-SERVICE/api/accounts";
  static const String deleteAccountUri = "/ACCOUNT-SERVICE/api/accounts";

  static const String getTransactionsUri = "/TRANSACTIONS-SERVICE/user/transactions";
  static const String transferTransactionUri = "/TRANSACTIONS-SERVICE/user/transactions/transfer";
  static const String getTransactionByReferenceUri = "/TRANSACTIONS-SERVICE/user/transactions/reference";

  static const String assistantMessageUri = "/ASSISTANT-SERVICE/assistant/message";
  static const String getMessagesUri = "/ASSISTANT-SERVICE/assistant/messages";


  static const String markNotificationAsReadUri = '/NOTIFICATIONS-SERVICE/notifications/mark-read';
  static const String sendNotificationUri = '/NOTIFICATIONS-SERVICE/notifications/send';
  static const String getNotificationsUri = '/NOTIFICATIONS-SERVICE/notifications';

  static const String addRecipientUri = '/RECIPIENT-SERVICE/api/recipients';
  static const String updateRecipientUri = '/RECIPIENT-SERVICE/api/recipients';
  static const String deleteRecipientUri = '/RECIPIENT-SERVICE/api/recipients';
  static const String getRecipientsUri = '/RECIPIENT-SERVICE/api/recipients';

}