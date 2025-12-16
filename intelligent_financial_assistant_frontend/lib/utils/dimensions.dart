import 'package:get/get.dart';

class Dimensions {

  static double get width => Get.context?.width ?? 400;

  // Padding Sizes
  static double get paddingSizeExtraSmall => width <= 400 ? 4 : 5.0;
  static double get paddingSizeMin => width <= 400 ? 6 : 7.0;
  static double get paddingSizeSmall => width <= 400 ? 8 : 10.0;
  static double get paddingSizeLarge => width <= 400 ? 18 : 20.0;
  static double get paddingSizeOverLarge => width <= 400 ? 45 : 50.0;
  static double paddingSizeDefault = 15.0;

  // Font Sizes
  static double get fontSizeExtraSmall => width <= 400 ? 10.0 : 10.0;
  static double get fontSizeSmall => width <= 400 ? 12 : 12.0;
  static double get fontSizeDefault => width <= 400 ? 14 : 14.0;
  static double get fontSizeLarge => width <= 400 ? 14 : 16.0;
  static double get fontSizeExtraLarge => width <= 400 ? 16 : 18.0;
  static double get fontSizeHeading => width <= 400 ? 18 : 22.0;
  static double get fontSizeOverLarge => width <= 400 ? 20 : 24.0;
  static double get fontSizeHeadingLarge => width <= 400 ? 24 : 30.0;

  // Icon sizes (keep static const, they don't depend on screen)
  static const double iconSizeSmall = 3.0;
  static const double iconSizeMedium = 15.0;
  static const double iconSizeMediumSmall = 10.0;
  static const double iconSizeDefault = 17.0;
  static const double iconSizeMenu = 25.0;
  static const double iconSizeLarge = 30.0;
  static const double iconSizeExtraLarge = 50.0;
}
