abstract class NotificationsServiceInterface {
  Future<void> sendNotification(Map<String, dynamic> data);
  Future<List<String>> fetchNotifications();
  Future<void> markAsRead(int notificationId);
}