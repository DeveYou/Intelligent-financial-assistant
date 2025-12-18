import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/error_boundary.dart';
import 'package:intelligent_financial_assistant_frontend/di/dependency_injection.dart' as di;
import 'package:intelligent_financial_assistant_frontend/features/account/controllers/account_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/controllers/assistant_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/home/controllers/home_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/controllers/notifications_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/recipient/controllers/recipient_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/settings/controllers/settings_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/splash/screens/splash_screen.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intelligent_financial_assistant_frontend/features/transaction/controllers/transaction_controller.dart';
import 'package:intelligent_financial_assistant_frontend/firebase_options.dart';
import 'package:intelligent_financial_assistant_frontend/helpers/fallback_localization_delegate.dart';
import 'package:intelligent_financial_assistant_frontend/localization/app_localization.dart';
import 'package:intelligent_financial_assistant_frontend/localization/controllers/localization_controller.dart';
import 'package:intelligent_financial_assistant_frontend/push_notification/helpers/notification_helper.dart';
import 'package:intelligent_financial_assistant_frontend/push_notification/models/notification_body.dart';
import 'package:intelligent_financial_assistant_frontend/theme/controllers/theme_controller.dart';
import 'package:intelligent_financial_assistant_frontend/theme/dark_mode.dart';
import 'package:intelligent_financial_assistant_frontend/theme/light_mode.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';
import 'package:provider/provider.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  try {
    await dotenv.load(fileName: ".env");
  } catch (e) {
    debugPrint("Warning: .env file not found. API keys may be missing.");
  }

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  await di.init();
  dynamic providers = [
    ChangeNotifierProvider(create: (context) => di.sl<LocalizationController>()),
    ChangeNotifierProvider(create: (context) => di.sl<ThemeController>()),
    ChangeNotifierProvider(create: (context) => di.sl<AuthenticationController>()),
    ChangeNotifierProvider(create: (context) => di.sl<HomeController>()),
    ChangeNotifierProvider(create: (context) => di.sl<AccountController>()),
    ChangeNotifierProvider(create: (context) => di.sl<AssistantController>()),
    ChangeNotifierProvider(create: (context) => di.sl<NotificationsController>()),
    ChangeNotifierProvider(create: (context) => di.sl<SettingsController>()),
    ChangeNotifierProvider(create: (context) => di.sl<TransactionController>()),
    ChangeNotifierProvider(create: (context) => di.sl<RecipientController>())
  ];

  flutterLocalNotificationsPlugin.resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()?.requestNotificationsPermission();
  NotificationBody? notificationBody;

  try {
    final RemoteMessage? initialMessage = await FirebaseMessaging.instance.getInitialMessage();
    if (initialMessage != null) {
      notificationBody = NotificationHelper.convertNotification(initialMessage.data);
    }
    await NotificationHelper.initialize(flutterLocalNotificationsPlugin);
    FirebaseMessaging.onBackgroundMessage(NotificationHelper.backgroundMessageHandler);
  } catch (e) {
    debugPrint('Error during initialize notifications : $e');
  }

  SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]).then((value) => {
    runApp(
        ErrorBoundary(
          child: MultiProvider(
              providers: providers,
              child: MyApp(notificationBody : notificationBody,)
          ),
        )
    )
  });
}

class MyApp extends StatelessWidget {
  final NotificationBody? notificationBody;
  const MyApp({super.key, required this.notificationBody});

  @override
  Widget build(BuildContext context) {
    final List<Locale> locales = [];
    for (var language in AppConstants.languages) {
      locales.add(Locale(language.languageCode!, language.countryCode));
    }

    return MaterialApp(
      title: AppConstants.appName,
      navigatorKey: navigatorKey,
      debugShowCheckedModeBanner: false,
      theme: Provider.of<ThemeController>(context).isDarkMode ? dark : light,
      locale: Provider.of<LocalizationController>(context).locale,
      localizationsDelegates: [
        AppLocalization.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        FallbackLocalizationDelegate(),
      ],
      builder: (context, child) {
        return MediaQuery(
          data: MediaQuery.of(context).copyWith(textScaler: TextScaler.noScaling),
          child: child!,
        );
      },
      supportedLocales: locales,
      home: SplashScreen(),
    );
  }
}


class Get {
  static BuildContext? get context => navigatorKey.currentContext;
  static NavigatorState? get navigator => navigatorKey.currentState;
}