import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/theme/controllers/theme_controller.dart';
import 'package:provider/provider.dart';

const TextStyle textRegular = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontWeight: FontWeight.w300,
  fontSize: 14,
);

const TextStyle textMedium = TextStyle(
    fontFamily: 'SF-Pro-Rounded-Regular',
    fontSize: 14,
    fontWeight: FontWeight.w500
);

const TextStyle textBold = TextStyle(
    fontFamily: 'SF-Pro-Rounded-Regular',
    fontSize: 14,
    fontWeight: FontWeight.w600
);

const TextStyle robotoBold = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontSize: 14,
  fontWeight: FontWeight.w700,
);

const TextStyle titleRegular = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontWeight: FontWeight.w500,
  fontSize: 14,
);

const TextStyle titleHeader = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontWeight: FontWeight.w600,
  fontSize: 16,
);

const TextStyle textSemiBold = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontSize: 12,
  fontWeight: FontWeight.w600,
);


class ThemeShadow {
  static List <BoxShadow> getShadow(BuildContext context) {
    List<BoxShadow> boxShadow =  [BoxShadow(color: Provider.of<ThemeController>(context, listen: false).isDarkMode? Colors.black26:
    Theme.of(context).primaryColor.withValues(alpha: .075), blurRadius: 5,spreadRadius: 1,offset: const Offset(1,1))];
    return boxShadow;
  }
}