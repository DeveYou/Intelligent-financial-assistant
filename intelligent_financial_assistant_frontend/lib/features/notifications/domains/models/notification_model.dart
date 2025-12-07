class NotificationModel {
  String? title;
  String? message;
  DateTime? timestamp;
  bool? isRead;

  NotificationModel({
    this.title,
    this.message,
    this.timestamp,
    this.isRead = false,
  });

  NotificationModel.fromJson(Map<String, dynamic> json) {
    title = json['title'];
    message = json['message'];
    timestamp = json['timestamp'] != null
        ? DateTime.parse(json['timestamp'])
        : null;
    isRead = json['is_read'] ?? false;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['title'] = title;
    data['message'] = message;
    data['timestamp'] = timestamp?.toIso8601String();
    data['is_read'] = isRead;
    return data;
  }
}