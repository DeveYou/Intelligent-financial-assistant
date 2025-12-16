import 'package:flutter/material.dart';

ThemeData dark = ThemeData(
  brightness: Brightness.dark,
  primaryColor: const Color(0xFF1E1E1E),

  cardColor: const Color(0xFF1E1E1E),
  hintColor: const Color(0xFF9E9E9E),
  dividerColor: const Color(0xFF2C2C2C),
  disabledColor: const Color(0xFF6E6E6E),

  scaffoldBackgroundColor: const Color(0xFF121212),

  colorScheme: ColorScheme.fromSwatch().copyWith(
    brightness: Brightness.dark,
    primary: const Color(0xFF1E1E1E),
    secondary: const Color(0xFFFFBD09),
    error: const Color(0xFFCF6679),
  ),

  textTheme: const TextTheme(
    headlineSmall: TextStyle(color: Colors.white), // AppBar title
    titleLarge: TextStyle(color: Colors.white),    // BottomSheet title
    titleMedium: TextStyle(color: Colors.white),   // Section titles
    bodyLarge: TextStyle(color: Colors.white),     // List tile titles
    bodyMedium: TextStyle(color: Colors.white70),  // Secondary text
    bodySmall: TextStyle(color: Colors.white54),   // Section headers
  ),

  appBarTheme: const AppBarTheme(
    backgroundColor: Color(0xFF1E1E1E),
    foregroundColor: Colors.white,
    elevation: 0,
    iconTheme: IconThemeData(color: Colors.white),
  ),

  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: const Color(0xFF1E1E1E),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      textStyle: const TextStyle(color: Colors.white, fontSize: 18),
    ),
  ),
);