import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/splash/screens/splash_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await FlutterLocalization.instance.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final FlutterLocalization localization = FlutterLocalization.instance;

    @override
    void initState() {
      localization.init(
        mapLocales: [
          const MapLocale('en', AppLocale.EN),
          const MapLocale('km', AppLocale.KM),
          const MapLocale('ja', AppLocale.JA),
        ],
        initLanguageCode: 'en',
      );
      localization.onTranslatedLanguage = _onTranslatedLanguage;
      super.initState();
    }

    void _onTranslatedLanguage(Locale? locale) {
      setState(() {});
    }
    return MaterialApp(
      title: 'Intelligent financial assistant',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      home: const  SplashScreen(),
    );
  }
}