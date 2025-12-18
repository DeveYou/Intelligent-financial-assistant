/// Represents a push notification in the application.
///
/// Notifications contain a title, message, timestamp, and read status.
class NotificationModel {
  /// Unique identifier for the notification.
  int? id;
  
  /// Title of the notification.
  String? title;
  
  /// Message/body content of the notification.
  String? message;
  
  /// When the notification was created/sent.
  DateTime? timestamp;
  
  /// Whether the notification has been read by the user.
  bool? isRead;

  /// Creates a new [NotificationModel] instance.
  NotificationModel({
    this.id,
    this.title,
    this.message,
    this.timestamp,
    this.isRead = false,
  });

  /// Creates a [NotificationModel] from JSON data.
  NotificationModel.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    title = json['title'];
    message = json['message'];
    timestamp = json['timestamp'] != null
        ? DateTime.parse(json['timestamp'])
        : null;
    isRead = json['is_read'] ?? false;
  }

  /// Converts this [NotificationModel] to a JSON map.
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['title'] = title;
    data['message'] = message;
    data['timestamp'] = timestamp?.toIso8601String();
    data['is_read'] = isRead;
    return data;
  }
}