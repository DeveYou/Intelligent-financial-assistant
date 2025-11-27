import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:intelligent_financial_assistant_frontend/push_notification/models/notification_body.dart';

class NotificationHelper {

  static Future<void> initialize(FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin) async {
    const AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    final InitializationSettings initializationSettings =
        InitializationSettings(
      android: initializationSettingsAndroid,
    );

    await flutterLocalNotificationsPlugin.initialize(initializationSettings);

  }

  static NotificationBody? convertNotification(Map<String, dynamic> data) {
    if (data['title'] == 'transaction') {
      return NotificationBody(title: 'transaction');
    } else if (data['title'] == 'reminder') {
      return NotificationBody(title: 'reminder');
    } else if (data['title'] == 'alert') {
      return NotificationBody(title: 'alert');
    } else if (data['title'] == 'news') {
      return NotificationBody(title: 'news');
    } else {
      return null;
    }
  }

  static Future<dynamic> backgroundMessageHandler(RemoteMessage message) async {
    if (kDebugMode) {
      print("onBackground: ${message.notification!.title}/${message.notification!.body}/${message.notification!.titleLocKey}");
    }

    var androidInitialize = AndroidInitializationSettings('notification_icon');
    var iOSInitialize = DarwinInitializationSettings();
    var initializationsSettings = InitializationSettings(android: androidInitialize, iOS: iOSInitialize);
    FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
    await flutterLocalNotificationsPlugin.initialize(initializationsSettings);
    NotificationHelper.showNotification(message, flutterLocalNotificationsPlugin, true);
  }

  static Future<void> showNotification(RemoteMessage message, FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin, bool bool) async {
    String? title;
    String? body;

    if (bool) {
      title = message.notification?.title;
      body = message.notification?.body;
    } else {
      title = message.data['title'];
      body = message.data['body'];
    }
  }
}