
import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/localization/app_localization.dart';

String? getTranslated(String? key, BuildContext context) {
  String? text = key;
  try {
    text = AppLocalization.of(context)!.translate(key!);
  } catch (e) {
    text = key;
  }
  return text;
}