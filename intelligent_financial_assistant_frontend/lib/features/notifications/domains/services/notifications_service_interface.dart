import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Service interface for notification operations.
///
/// Handles sending notifications, fetching user notifications,
/// and marking notifications as read.
abstract class NotificationsServiceInterface {
  /// Sends a new notification.
  ///
  /// [data] contains the notification details (title, message, etc.).
  Future<ApiResponse> sendNotification(Map<String, dynamic> data);
  
  /// Retrieves all notifications for the current user.
  Future<ApiResponse> fetchNotifications(int userId);
  
  /// Marks a notification as read.
  ///
  /// [notificationId] is the ID of the notification to mark as read.
  Future<ApiResponse> markAsRead(int notificationId);
}