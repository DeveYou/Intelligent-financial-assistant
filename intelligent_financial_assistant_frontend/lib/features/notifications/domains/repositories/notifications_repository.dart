import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/repositories/notifications_repository_interface.dart';

class NotificationsRepository implements NotificationsRepositoryInterface{
  @override
  Future<List<String>> fetchNotifications() {
    // TODO: implement fetchNotifications
    throw UnimplementedError();
  }

  @override
  Future<void> markAsRead(int notificationId) {
    // TODO: implement markAsRead
    throw UnimplementedError();
  }

  @override
  Future<void> sendNotification(Map<String, dynamic> data) {
    // TODO: implement sendNotification
    throw UnimplementedError();
  }

}