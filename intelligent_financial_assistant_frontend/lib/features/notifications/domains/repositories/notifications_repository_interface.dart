abstract class NotificationsRepositoryInterface {
  Future<void> sendNotification(Map<String, dynamic> data);
  Future<List<String>> fetchNotifications();
  Future<void> markAsRead(int notificationId);
}