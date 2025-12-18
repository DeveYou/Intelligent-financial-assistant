import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/theme/controllers/theme_controller.dart';
import 'package:intelligent_financial_assistant_frontend/utils/custom_themes.dart';
import 'package:intelligent_financial_assistant_frontend/utils/dimensions.dart';
import 'package:provider/provider.dart';

/// A customizable button widget with various styling options.
///
/// Supports different styles including bordered, colored backgrounds,
/// custom text colors, and optional left icons.
class CustomButton extends StatelessWidget {
  /// Callback function triggered when the button is tapped.
  final Function()? onTap;
  
  /// Text displayed on the button.
  final String? buttonText;
  
  /// Whether this is a "buy" button (uses special color).
  final bool isBuy;
  
  /// Whether to show a border around the button.
  final bool isBorder;
  
  /// Custom background color for the button.
  final Color? backgroundColor;
  
  /// Custom text color.
  final Color? textColor;
  
  /// Custom border color (when [isBorder] is true).
  final Color? borderColor;
  
  /// Border radius for rounded corners.
  final double? radius;
  
  /// Font size for button text.
  final double? fontSize;
  
  /// Path to an optional icon image displayed on the left.
  final String? leftIcon;
  
  /// Width of the border (when [isBorder] is true).
  final double? borderWidth;

  /// Creates a [CustomButton] with the specified properties.
  const CustomButton({super.key, this.onTap, required this.buttonText, this.isBuy= false, this.isBorder = false, this.backgroundColor, this.radius, this.textColor, this.fontSize, this.leftIcon, this.borderColor, this.borderWidth});

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: onTap,
      style: TextButton.styleFrom(padding: const EdgeInsets.all(0)),
      child: Container(height: 45,
        alignment: Alignment.center,
        decoration: BoxDecoration(
            border: isBorder? Border.all(color: borderColor??Theme.of(context).primaryColor, width:  borderWidth ??1): null,
            color:  backgroundColor ?? (isBuy? const Color(0xFFF44E1E) : Theme.of(context).primaryColor),
            borderRadius: BorderRadius.circular(radius !=null ? radius! : isBorder? Dimensions.paddingSizeExtraSmall : Dimensions.paddingSizeSmall)),
        child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [
          if(leftIcon != null)
            Padding(padding: const EdgeInsets.only(right: 5),
              child: SizedBox(width: 30, child: Padding(
                padding: EdgeInsets.all(Dimensions.paddingSizeExtraSmall),
                child: Image.asset(leftIcon!),
              )),
            ),
          Flexible(
            child: Text(buttonText??"", style: textSemiBold.copyWith(fontSize: fontSize?? 16,
              color: textColor ?? (Provider.of<ThemeController>(context, listen: false).isDarkMode? Colors.white : Theme.of(context).highlightColor),
            )),
          ),
        ],
        ),
      ),
    );
  }
}
