import 'dart:developer' as developer;

import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

/// Displays a custom snackbar/toast message to the user.
///
/// Shows a toast notification at the bottom of the screen with customizable
/// error/success styling.
///
/// [message] is the text to display.
/// [context] is the BuildContext (currently unused but kept for compatibility).
/// [isError] determines the background color (red for errors, green for success).
/// [isToaster] is currently unused but kept for future enhancements.
void showCustomSnackBarWidget(String? message, BuildContext context, {bool isError = true, bool isToaster = false}) {

  developer.log('Showing snackbar message: $message (isError: $isError)');

  Fluttertoast.showToast(
      msg: message!,
      toastLength: Toast.LENGTH_SHORT,
      gravity: ToastGravity.BOTTOM,
      timeInSecForIosWeb: 1,
      backgroundColor: isError ? const Color(0xFFFF0014) : const Color(0xFF1E7C15),
      textColor: Colors.white,
      fontSize: 16.0
  );
}