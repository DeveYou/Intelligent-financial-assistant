import 'package:flutter/material.dart';

ThemeData light = ThemeData(
  brightness: Brightness.light,
  primaryColor: const Color(0xFFF44E1E),

  cardColor: const Color(0xFFFFFFFF),
  hintColor: const Color(0xFF9E9E9E),
  dividerColor: const Color(0xFFE0E0E0),
  disabledColor: const Color(0xFF9E9E9E),

  scaffoldBackgroundColor: Colors.white,

  colorScheme: ColorScheme.fromSwatch().copyWith(
    brightness: Brightness.light,
    primary: const Color(0xFFF44E1E),
    secondary: const Color(0xFFFFBD09),
    error: const Color(0xFFB00020),
  ),

  textTheme: const TextTheme(
    headlineSmall: TextStyle(color: Colors.black),
    titleLarge: TextStyle(color: Colors.black),
    titleMedium: TextStyle(color: Colors.black87),
    bodyLarge: TextStyle(color: Colors.black87),
    bodyMedium: TextStyle(color: Colors.black54),
    bodySmall: TextStyle(color: Colors.black54),
  ),

  appBarTheme: const AppBarTheme(
    backgroundColor: Color(0xFFF44E1E),
    foregroundColor: Colors.white,
    elevation: 0,
    iconTheme: IconThemeData(color: Colors.white),
  ),

  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: const Color(0xFFF44E1E),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      textStyle: const TextStyle(color: Colors.white, fontSize: 18),
    ),
  ),
);