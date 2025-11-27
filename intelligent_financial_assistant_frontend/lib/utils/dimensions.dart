import 'package:get/get.dart';

class Dimensions {
  // Padding Sizes
  static double paddingSizeExtraSmall = Get.context!.width <= 400 ? 4 : 5.0;
  static double paddingSizeMin = Get.context!.width <= 400 ? 6 : 7.0;
  static double paddingSizeSmall = Get.context!.width <= 400 ? 8 : 10.0;


  // Font Sizes
  static double fontSizeExtraSmall = Get.context!.width <= 400 ? 10.0 : 10.0;
  static double fontSizeSmall = Get.context!.width <= 400 ? 12 : 12.0;
  static double fontSizeDefault = Get.context!.width <= 400 ? 14 : 14.0;
  static double fontSizeLarge = Get.context!.width <= 400 ? 14 : 16.0;
  static double fontSizeExtraLarge = Get.context!.width <= 400 ? 16 : 18.0;
  static double fontSizeHeading = Get.context!.width <= 400 ? 18 : 22.0;
  static double fontSizeOverLarge = Get.context!.width <= 400 ? 20 : 24.0;
  static double fontSizeHeadingLarge = Get.context!.width <= 400 ? 24 : 30.0;


  // Icon Sizes
  static const double iconSizeSmall = 3.0;
  static const double iconSizeMedium = 15.0;
  static const double iconSizeMediumSmall = 10.0;
  static const double iconSizeDefault = 17.0;
  static const double iconSizeMenu = 22.0;
  static const double iconSizeLarge = 30.0;
  static const double iconSizeExtraLarge = 50.0;
}