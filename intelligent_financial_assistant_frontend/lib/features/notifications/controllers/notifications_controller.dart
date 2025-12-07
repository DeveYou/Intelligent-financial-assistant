import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/push_notification/services/notification_service.dart';

class NotificationsController with ChangeNotifier{
  final NotificationService notificationService;

  NotificationsController({required this.notificationService});



}