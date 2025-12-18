import 'dart:convert';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/screens/notifications_screen.dart';
import 'package:intelligent_financial_assistant_frontend/push_notification/models/notification_body.dart';
import '../../main.dart';

/// Helper class for managing local push notifications.
///
/// Handles initializing the notification system, displaying notifications,
/// and processing notification payloads.
class NotificationHelper {

  /// Initializes the local notifications plugin with platform-specific settings.
  ///
  /// Sets up notification channels and click handlers for both Android and iOS.
  static Future<void> initialize(FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin) async {
    var androidInitialize = const AndroidInitializationSettings('@mipmap/ic_launcher');

    var iOSInitialize = const DarwinInitializationSettings();
    var initializationsSettings = InitializationSettings(android: androidInitialize, iOS: iOSInitialize);

    await flutterLocalNotificationsPlugin.initialize(
      initializationsSettings,
      onDidReceiveNotificationResponse: (NotificationResponse response) async {
        try {
          if (response.payload != null && response.payload!.isNotEmpty) {
            Navigator.of(Get.context!).push(
                MaterialPageRoute(
                    builder: (context) => const NotificationsScreen()
                )
            );
          }
        } catch (e) {
          debugPrint('Error navigating to notification screen: $e');
        }
      },
    );

    // Create High Priority Channel
    const AndroidNotificationChannel channel = AndroidNotificationChannel(
      'bank_notifications', // id
      'VoxBank Notifications', // title
      description: 'High priority notifications for VoxBank.',
      importance: Importance.max, // Importance.max for Heads-up
    );

    await flutterLocalNotificationsPlugin
        .resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(channel);
  }

  /// Displays a notification to the user.
  ///
  /// [message] contains the notification data including title and body.
  /// [fln] is the notification plugin instance to use for showing the notification.
  /// Displays a notification to the user.
  ///
  /// [message] contains the notification data including title and body.
  /// [fln] is the notification plugin instance to use for showing the notification.
  static Future<void> showNotification(Map<String, dynamic> message, FlutterLocalNotificationsPlugin fln) async {
    // Check for 'message' (from data payload/model) or fallback to 'body'
    String? body = message['message'] ?? message['body'];
    String? title = message['title'];

    if (title == null || body == null) return;

    String? payload = jsonEncode(message);

    // Android Channel Configuration
    const AndroidNotificationDetails androidPlatformChannelSpecifics = AndroidNotificationDetails(
      'bank_notifications', // channel id matches the one created
      'VoxBank Notifications', // channel name matches
      channelDescription: 'Notifications for transactions and account updates',
      importance: Importance.max,
      priority: Priority.high,
      playSound: true,
      styleInformation: BigTextStyleInformation(''),
    );

    const DarwinNotificationDetails iOSPlatformChannelSpecifics = DarwinNotificationDetails();

    const NotificationDetails platformChannelSpecifics = NotificationDetails(
        android: androidPlatformChannelSpecifics,
        iOS: iOSPlatformChannelSpecifics
    );

    await fln.show(0, title, body, platformChannelSpecifics, payload: payload);
  }

  /// Handles background messages when the app is not in the foreground.
  ///
  /// Initializes a new notification plugin instance and displays the notification.
  static Future<void> backgroundMessageHandler(RemoteMessage message) async {
    if (kDebugMode) {
      print("onBackground: ${message.data}");
    }

    var androidInitialize = const AndroidInitializationSettings('@mipmap/ic_launcher');
    var iOSInitialize = const DarwinInitializationSettings();
    var initializationsSettings = InitializationSettings(android: androidInitialize, iOS: iOSInitialize);
    FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
    await flutterLocalNotificationsPlugin.initialize(initializationsSettings);
    
    // Pass the data payload which contains 'message'
    NotificationHelper.showNotification(message.data, flutterLocalNotificationsPlugin);
  }

  /// Converts notification data into a [NotificationBody] object.
  ///
  /// Maps notification types (transaction, reminder, alert, news) to NotificationBody objects.
  /// Returns null if the notification type is not recognized.
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

}