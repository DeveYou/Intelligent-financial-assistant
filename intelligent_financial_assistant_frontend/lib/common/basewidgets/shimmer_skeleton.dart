import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';

/// A shimmer loading skeleton widget for displaying loading states.
///
/// Provides animated placeholders while content is loading.
/// Supports both rectangular and circular shapes.
class ShimmerSkeleton extends StatelessWidget {
  /// Height of the skeleton widget.
  final double height;
  
  /// Width of the skeleton widget.
  final double width;
  
  /// Border radius for rounded corners (used for rectangular shape).
  final double borderRadius;
  
  /// Shape of the skeleton (rectangular or circular).
  final ShapeBorder shapeBorder;

  /// Creates a rectangular shimmer skeleton.
  ///
  /// [height] and [width] define the skeleton dimensions.
  /// [borderRadius] sets the corner radius (default: 16).
  const ShimmerSkeleton.rectangular({
    super.key,
    required this.height,
    required this.width,
    this.borderRadius = 16,
  }) : shapeBorder = const RoundedRectangleBorder();

  /// Creates a circular shimmer skeleton.
  ///
  /// [height] and [width] should typically be equal for a perfect circle.
  const ShimmerSkeleton.circular({
    super.key,
    required this.height,
    required this.width,
    this.borderRadius = 16,
  }) : shapeBorder = const CircleBorder();

  @override
  Widget build(BuildContext context) {
    return Shimmer.fromColors(
      baseColor: Colors.grey[300]!,
      highlightColor: Colors.grey[100]!,
      period: const Duration(seconds: 2),
      child: Container(
        width: width,
        height: height,
        decoration: BoxDecoration(
          color: Colors.grey[400]!,
          borderRadius: shapeBorder == const CircleBorder() ? null : BorderRadius.circular(borderRadius),
          shape: shapeBorder == const CircleBorder() ? BoxShape.circle : BoxShape.rectangle,
        ),
      ),
    );
  }
}
