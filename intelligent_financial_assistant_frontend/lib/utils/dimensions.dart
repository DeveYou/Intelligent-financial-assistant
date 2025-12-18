import 'package:get/get.dart';

/// Provides responsive dimensions for the application UI.
///
/// This class contains static getters that calculate padding, font sizes, and icon sizes
/// based on the current screen width, ensuring a responsive design across different device sizes.
class Dimensions {

  /// The current screen width.
  static double get width => Get.context?.width ?? 400;

  // Padding Sizes
  
  /// Extra small padding size (4-5px).
  static double get paddingSizeExtraSmall => width <= 400 ? 4 : 5.0;
  
  /// Minimum padding size (6-7px).
  static double get paddingSizeMin => width <= 400 ? 6 : 7.0;
  
  /// Small padding size (8-10px).
  static double get paddingSizeSmall => width <= 400 ? 8 : 10.0;
  
  /// Large padding size (18-20px).
  static double get paddingSizeLarge => width <= 400 ? 18 : 20.0;
  
  /// Extra large padding size (45-50px).
  static double get paddingSizeOverLarge => width <= 400 ? 45 : 50.0;
  
  /// Default padding size (15px).
  static double paddingSizeDefault = 15.0;

  // Font Sizes
  
  /// Extra small font size (10px).
  static double get fontSizeExtraSmall => width <= 400 ? 10.0 : 10.0;
  
  /// Small font size (12px).
  static double get fontSizeSmall => width <= 400 ? 12 : 12.0;
  
  /// Default font size (14px).
  static double get fontSizeDefault => width <= 400 ? 14 : 14.0;
  
  /// Large font size (14-16px).
  static double get fontSizeLarge => width <= 400 ? 14 : 16.0;
  
  /// Extra large font size (16-18px).
  static double get fontSizeExtraLarge => width <= 400 ? 16 : 18.0;
  
  /// Heading font size (18-22px).
  static double get fontSizeHeading => width <= 400 ? 18 : 22.0;
  
  /// Extra large heading font size (20-24px).
  static double get fontSizeOverLarge => width <= 400 ? 20 : 24.0;
  
  /// Large heading font size (24-30px).
  static double get fontSizeHeadingLarge => width <= 400 ? 24 : 30.0;

  // Icon sizes (keep static const, they don't depend on screen)
  
  /// Small icon size (3px).
  static const double iconSizeSmall = 3.0;
  
  /// Medium icon size (15px).
  static const double iconSizeMedium = 15.0;
  
  /// Medium-small icon size (10px).
  static const double iconSizeMediumSmall = 10.0;
  
  /// Default icon size (17px).
  static const double iconSizeDefault = 17.0;
  
  /// Menu icon size (25px).
  static const double iconSizeMenu = 25.0;
  
  /// Large icon size (30px).
  static const double iconSizeLarge = 30.0;
  
  /// Extra large icon size (50px).
  static const double iconSizeExtraLarge = 50.0;
}
