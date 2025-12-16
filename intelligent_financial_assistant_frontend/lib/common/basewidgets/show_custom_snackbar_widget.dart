import 'dart:developer' as developer;

import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';


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