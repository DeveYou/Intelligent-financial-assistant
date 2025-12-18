import 'package:intelligent_financial_assistant_frontend/localization/models/language_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/images.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Application-wide constants including API endpoints, configuration values, and shared preferences keys.
///
/// This class centralizes all constant values used throughout the application,
/// making it easier to maintain and update configuration settings.
class AppConstants {

  /// The name of the application displayed to users.
  static const String appName = "Intelligent Financial Assistant";
  
  /// The current version of the application.
  static const double appVersion = 1.0;

  /// The base URL for all backend API requests.
  static const String baseUrl = "https://exhilaratingly-uncompiled-laree.ngrok-free.dev";
  
  /// Retrieves the Gemini API token from environment variables.
  ///
  /// Throws an [Exception] if the GEMINI_API_KEY is not found in the .env file.
  static String get geminiApiToken {
    final String? key = dotenv.env['GEMINI_API_KEY'];
    if (key == null || key.isEmpty) {
      throw Exception('GEMINI_API_KEY is missing in .env');
    }
    return key;
  }
  
  /// The Gemini model identifier used for AI assistant features.
  static const String geminiModel = "gemini-flash-latest";


  /// List of supported languages in the application.
  ///
  /// Each language includes a flag image, language name, country code, and language code.
  static List<LanguageModel> languages = [
    LanguageModel(imageUrl: Images.fr, languageName: 'French', countryCode: 'FR', languageCode: 'fr'),
    LanguageModel(imageUrl: Images.ar, languageName: 'Arabic', countryCode: 'MA', languageCode: 'ar'),
    LanguageModel(imageUrl: Images.en, languageName: 'English', countryCode: 'US', languageCode: 'en'),
    LanguageModel(imageUrl: Images.es, languageName: 'Spanish', countryCode: 'ES', languageCode: 'es'),
  ];

  // Shared preferences keys
  
  /// Key for storing the user's theme preference.
  static const String theme = "theme";
  
  /// Key for storing the authentication token.
  static const String token = "token";
  
  /// Key for storing the user's ID.
  static const String userId = "user_id";
  
  /// Key for storing the country code.
  static const String countryCode = 'country_code';
  
  /// Key for storing the language code.
  static const String languageCode = "language_code";
  
  /// Key for language setting.
  static const String langKey = 'lang';
  
  /// HTTP header key for localization.
  static const String localizationKey = 'X-localization';
  
  /// Key for storing the user's email.
  static const String userEmail = "user_email";
  
  /// Key for storing the user's password.
  static const String userPassword = "user_password";
  
  /// Key for notification sound preference.
  static const String notificationSound = "sound";
  
  /// Key for storing notification count.
  static const String notificationCount = "count";
  
  /// Key for storing the user login token.
  static const String userLoginToken = 'user_login_token';
  
  /// Firebase topic for financial assistant notifications.
  static const String topic = 'financial_assistant';
  
  /// Firebase topic for demo reset notifications.
  static const String demoTopic = 'demo_reset';
  
  /// Key for tracking if this is the user's first time opening the app.
  static const String kIsFirstTime = 'is_first_time';
  
  /// Key for tracking if the user is currently logged in.
  static const String kIsLoggedIn = 'is_logged_in';


  // API Endpoints
  
  /// Authentication service endpoint for user login.
  static const String loginUri = "/auth-service/auth/login";
  /// Authentication service endpoint for user registration.
  static const String registerUri = "/auth-service/auth/register";
  
  /// Authentication service endpoint for retrieving user information.
  static const String userInfoUri = "/auth-service/auth/user-info"; 
  
  /// Authentication service endpoint for user logout.
  static const String logoutUri = "/auth-service/auth/logout";
  
  /// Authentication service endpoint for initiating password reset.
  static const String forgotPasswordUri = "/auth-service/auth/forgot-password"; 
  
  /// Authentication service endpoint for resetting password.
  static const String resetPasswordUri = "/auth-service/auth/reset-password"; 
  
  /// Authentication service endpoint for updating the authentication token.
  static const String updateTokenUri = "/auth-service/auth/update-token"; 
  
  /// Account service endpoint for retrieving user account data.
  static const String getAccountDataUri = "/account-service/api/accounts/user";


  /// Account service endpoint for updating account information.
  static const String updateAccountUri = "/account-service/api/accounts"; 
  
  /// Account service endpoint for resending card verification code.
  static const String resendCardCodeUri = "/account-service/api/accounts/resend-code";
  
  /// Account service endpoint for deleting an account.
  static const String deleteAccountUri = "/account-service/api/accounts"; 

  /// Transaction service endpoint for retrieving user's transactions.
  static const String getTransactionsUri = "/transactions-service/user/transactions/my-transactions";
  
  /// Base URI for transaction service operations.
  static const String transactionBaseUri = "/transactions-service/user/transactions";
  
  /// Transaction service endpoint for initiating a money transfer.
  static const String transferTransactionUri = "/transactions-service/user/transactions/transfer";
  
  /// Transaction service endpoint for deposit operations.
  static const String depositTransactionUri = "/transactions-service/user/transactions/deposit";
  
  /// Transaction service endpoint for withdrawal operations.
  static const String withdrawalTransactionUri = "/transactions-service/user/transactions/withdrawal";
  
  /// Transaction service endpoint for retrieving a transaction by reference.
  static const String getTransactionByReferenceUri = "/transactions-service/user/transactions/reference";

  /// Assistant service endpoint for sending messages to the AI assistant.
  static const String assistantMessageUri = "/assistant-service/assistant/message"; // Missing Service
  
  /// Assistant service endpoint for retrieving chat messages.
  static const String getMessagesUri = "/assistant-service/assistant/messages"; // Missing Service

  /// Notification service endpoint for marking a notification as read.
  static const String markNotificationAsReadUri = "/notification-service/notifications/mark-read";
  
  /// Notification service endpoint for sending notifications.
  static const String sendNotificationUri = "/notification-service/notifications/send";
  
  /// Notification service endpoint for retrieving all notifications.
  static const String getNotificationsUri = "/notification-service/api/v1/notifications";
  
  /// Notification service endpoint for registering FCM token.
  static const String registerTokenUri = "/notification-service/api/v1/notifications/save-token";

  /// Recipient service endpoint for adding a new recipient.
  static const String addRecipientUri = "/recipient-service/api/recipients";
  
  /// Recipient service endpoint for updating recipient information.
  static const String updateRecipientUri = "/recipient-service/api/recipients";
  
  /// Recipient service endpoint for deleting a recipient.
  static const String deleteRecipientUri = "/recipient-service/api/recipients";
  
  /// Recipient service endpoint for retrieving all recipients.
  static const String getRecipientsUri = "/recipient-service/api/recipients";


  /// The transaction charge/fee amount.
  static const double transactionCharge = 10.0;

}