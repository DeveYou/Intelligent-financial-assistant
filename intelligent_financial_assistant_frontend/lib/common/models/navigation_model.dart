import 'package:flutter/material.dart';

/// Represents a navigation destination in the app's bottom navigation bar.
///
/// Contains the screen widget, icon path, and display name for a navigation item.
class NavigationModel {
  /// Display name for the navigation item.
  String name;
  
  /// Path to the icon image for this navigation item.
  String icon;
  
  /// The widget/screen to navigate to when this item is selected.
  Widget screen;
  
  /// Creates a [NavigationModel] with the specified properties.
  NavigationModel({required this.name, required this.icon,  required this.screen});
}