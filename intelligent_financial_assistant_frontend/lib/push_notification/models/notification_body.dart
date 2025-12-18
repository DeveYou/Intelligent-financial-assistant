/// Represents the data payload of a push notification.
///
/// Contains the basic information for a notification including ID, title, and message.
class NotificationBody {
  /// Unique identifier for the notification.
  int? id;
  
  /// Title/type of the notification (e.g., 'transaction', 'reminder', 'alert').
  String? title;
  
  /// The message content of the notification.
  String? message;

  /// Creates a [NotificationBody] instance.
  NotificationBody({this.id, this.title, this.message});

  /// Creates a [NotificationBody] from JSON data.
  NotificationBody.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    title = json['title'];
    message = json['message'];
  }

  /// Converts this [NotificationBody] to a JSON map.
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['title'] = title;
    data['message'] = message;
    return data;
  }
}