import 'package:intelligent_financial_assistant_frontend/localization/models/language_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/images.dart';
class AppConstants {

  static const String appName = "Intelligent Financial Assistant";
  static const double appVersion = 1.0;

  //static const String baseUrl = "http://localhost:8080";
  static const String baseUrl = "https://exhilaratingly-uncompiled-laree.ngrok-free.dev";

  static const String  geminiApiToken = "AIzaSyBgZFA2J_fYfD1VKn8P1s0FCCWircEY7kk";
  static const String geminiModel = "gemini-2.0-flash";


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
  static const String userId = "user_id";
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
  static const String loginUri = "/auth-service/auth/login";
  static const String registerUri = "/auth-service/auth/register";
  static const String userInfoUri = "/auth-service/auth/user-info"; 
  static const String logoutUri = "/auth-service/auth/logout";
  static const String forgotPasswordUri = "/auth-service/auth/forgot-password"; 
  static const String resetPasswordUri = "/auth-service/auth/reset-password"; 
  static const String updateTokenUri = "/auth-service/auth/update-token"; 
  static const String getAccountDataUri = "/account-service/api/accounts/user";

  static const String accountUri = "/account-service/api/accounts";
  static const String updateAccountUri = "/account-service/api/accounts"; 
  static const String resendCardCodeUri = "/account-service/api/accounts/resend-code";
  static const String deleteAccountUri = "/account-service/api/accounts"; 

  static const String getTransactionsUri = "/transactions-service/user/transactions/my-transactions";
  static const String transactionBaseUri = "/transactions-service/user/transactions";
  static const String transferTransactionUri = "/transactions-service/user/transactions/transfer";
  static const String depositTransactionUri = "/transactions-service/user/transactions/deposit";
  static const String withdrawalTransactionUri = "/transactions-service/user/transactions/withdrawal";
  static const String getTransactionByReferenceUri = "/transactions-service/user/transactions/reference";

  static const String assistantMessageUri = "/assistant-service/assistant/message"; // Missing Service
  static const String getMessagesUri = "/assistant-service/assistant/messages"; // Missing Service

  static const String markNotificationAsReadUri = "/notifications-service/notifications/mark-read";
  static const String sendNotificationUri = "/notifications-service/notifications/send";
  static const String getNotificationsUri = "/notifications-service/notifications";

  static const String addRecipientUri = "/recipient-service/api/recipients";
  static const String updateRecipientUri = "/recipient-service/api/recipients";
  static const String deleteRecipientUri = "/recipient-service/api/recipients";
  static const String getRecipientsUri = "/recipient-service/api/recipients";


  static const double transactionCharge = 10.0;

}