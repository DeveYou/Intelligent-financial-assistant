import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';

class ShimmerSkeleton extends StatelessWidget {
  final double height;
  final double width;
  final double borderRadius;
  final ShapeBorder shapeBorder;

  const ShimmerSkeleton.rectangular({
    super.key,
    required this.height,
    required this.width,
    this.borderRadius = 16,
  }) : shapeBorder = const RoundedRectangleBorder();

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
