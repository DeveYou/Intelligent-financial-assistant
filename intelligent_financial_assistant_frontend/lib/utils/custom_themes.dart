import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/theme/controllers/theme_controller.dart';
import 'package:provider/provider.dart';

const textRegular = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontWeight: FontWeight.w300,
  fontSize: 14,
);

const textMedium = TextStyle(
    fontFamily: 'SF-Pro-Rounded-Regular',
    fontSize: 14,
    fontWeight: FontWeight.w500
);

const textBold = TextStyle(
    fontFamily: 'SF-Pro-Rounded-Regular',
    fontSize: 14,
    fontWeight: FontWeight.w600
);

const robotoBold = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontSize: 14,
  fontWeight: FontWeight.w700,
);

const titleRegular = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontWeight: FontWeight.w500,
  fontSize: 14,
);

const titleHeader = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontWeight: FontWeight.w600,
  fontSize: 16,
);

const textSemiBold = TextStyle(
  fontFamily: 'SF-Pro-Rounded-Regular',
  fontSize: 12,
  fontWeight: FontWeight.w600,
);


class ThemeShadow {
  static List <BoxShadow> getShadow(BuildContext context) {
    List<BoxShadow> boxShadow =  [BoxShadow(color: Provider.of<ThemeController>(context, listen: false).isDarkMode? Colors.black26:
    Theme.of(context).primaryColor.withOpacity(.075), blurRadius: 5,spreadRadius: 1,offset: const Offset(1,1))];
    return boxShadow;
  }
}