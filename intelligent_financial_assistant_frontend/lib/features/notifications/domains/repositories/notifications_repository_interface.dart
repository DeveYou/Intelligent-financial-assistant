import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

abstract class NotificationsRepositoryInterface {
  Future<ApiResponse> sendNotification(Map<String, dynamic> data);
  Future<ApiResponse> fetchNotifications();
  Future<ApiResponse> markAsRead(int notificationId);
}