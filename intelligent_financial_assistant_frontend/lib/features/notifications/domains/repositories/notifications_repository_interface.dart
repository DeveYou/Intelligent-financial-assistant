import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Repository interface for notification data operations.
///
/// Handles direct API communication for notifications.
abstract class NotificationsRepositoryInterface {
  /// Sends a notification via the API.
  ///
  /// [data] contains the notification payload (title, message, recipients, etc.).
  Future<ApiResponse> sendNotification(Map<String, dynamic> data);
  
  Future<ApiResponse> registerToken(String token, int userId);
  
  /// Fetches all notifications for the current user from the API.
  Future<ApiResponse> fetchNotifications(int userId);
  
  /// Marks a notification as read via the API.
  ///
  /// [notificationId] is the ID of the notification to mark.
  Future<ApiResponse> markAsRead(int notificationId);
}