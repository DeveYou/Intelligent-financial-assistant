import 'dart:convert';

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/screens/notifications_screen.dart';
import 'package:intelligent_financial_assistant_frontend/push_notification/models/notification_body.dart';
import '../../main.dart';


class NotificationHelper {

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
  }

  static Future<void> showNotification(Map<String, dynamic> message, FlutterLocalNotificationsPlugin fln) async {
    if (message['title'] == null || message['body'] == null) return;

    String title = message['title'];
    String body = message['body'];
    String? payload = jsonEncode(message);

    // Android Channel Configuration
    const AndroidNotificationDetails androidPlatformChannelSpecifics = AndroidNotificationDetails(
      'voxbank_channel_id', // channel id
      'VoxBank Transaction Alerts', // channel name
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

  static Future<dynamic> backgroundMessageHandler(Map<String, dynamic> message) async {
    if (kDebugMode) {
      print("onBackground: ${message['title']}/${message['body']}");
    }

    var androidInitialize = AndroidInitializationSettings('notification_icon');
    var iOSInitialize = DarwinInitializationSettings();
    var initializationsSettings = InitializationSettings(android: androidInitialize, iOS: iOSInitialize);
    FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
    await flutterLocalNotificationsPlugin.initialize(initializationsSettings);
    NotificationHelper.showNotification(message, flutterLocalNotificationsPlugin);
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

}