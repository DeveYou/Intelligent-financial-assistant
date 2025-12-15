

import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/repositories/notifications_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/services/notifications_service_interface.dart';

class NotificationsService implements NotificationsServiceInterface {
  final NotificationsRepositoryInterface notificationsRepository;

  NotificationsService({required this.notificationsRepository});

  @override
  Future<ApiResponse> fetchNotifications() async {
    return await notificationsRepository.fetchNotifications();
  }

  @override
  Future<ApiResponse> markAsRead(int notificationId) async {
    return await notificationsRepository.markAsRead(notificationId);
  }

  @override
  Future<ApiResponse> sendNotification(Map<String, dynamic> data) async {
    return await notificationsRepository.sendNotification(data);
  }
}