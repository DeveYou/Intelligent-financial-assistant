import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/show_custom_snackbar_widget.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/models/notification_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/services/notifications_service_interface.dart';

import 'package:firebase_messaging/firebase_messaging.dart';
import '../../../main.dart';

/// Manages user notifications.
///
/// This controller handles fetching notifications and marking them as read.
class NotificationsController with ChangeNotifier {
  /// The service for notification operations.
  final NotificationsServiceInterface notificationService;

  /// Creates a [NotificationsController] with the required service.
  NotificationsController({required this.notificationService});

  bool _isLoading = false;
  List<NotificationModel>? _notificationList;

  /// Returns true if notifications are being loaded.
  bool get isLoading => _isLoading;

  /// Gets the list of notifications.
  List<NotificationModel>? get notificationList => _notificationList;

  /// Fetches the user's notifications from the API.
  ///
  /// [reload] forces a fresh fetch if true, otherwise uses cached data.
  Future<void> getNotificationsList(bool reload) async {
    if (_notificationList == null || reload) {
      _isLoading = true;
      notifyListeners();
    }

    int? userId;
    try {
      final authController = Provider.of<AuthenticationController>(Get.context!, listen: false);
      String userIdStr = authController.getUserId();
      if(userIdStr.isNotEmpty) {
        userId = int.parse(userIdStr);
      }
    } catch (e) {
      debugPrint("Error fetching userId: $e");
    }

    if (userId == null) {
       // If userId is missing, we can't fetch notifications correctly with the new backend logic.
       // Depending on requirements, we might return or try without ID (which will fail 400).
       // For now, let's proceed but log it.
       debugPrint("Warning: userId is null when fetching notifications");
       // return; // Optional: return early if ID is strictly required
       userId = 0; // Use 0 or handle as needed, but backend expects param.
    }

    ApiResponse apiResponse = await notificationService.fetchNotifications(userId);

    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _notificationList = [];
      apiResponse.response!.data.forEach((notification) {
        _notificationList!.add(NotificationModel.fromJson(notification));
      });
    } else {
      showCustomSnackBarWidget("Error: api response", Get.context!);
    }

    _isLoading = false;
    notifyListeners();
  }

  /// Marks a notification as read.
  ///
  /// [notificationId] is the ID of the notification to mark.
  /// [index] is the index in the notifications list for optimistic update.
  Future<void> markAsRead(int notificationId, int index) async {
    ApiResponse apiResponse = await notificationService.markAsRead(notificationId);
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _notificationList![index].isRead = true; // Optimistic update
      notifyListeners();
    }
  }

  /// Initialize Firebase listeners to handle incoming notifications in foreground.
  void initializeListeners() {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      print('Got a message whilst in the foreground!');
      print('Message data: ${message.data}');

      if (message.data.isNotEmpty) {
        // Parse the data payload into a NotificationModel
        try {
          NotificationModel newNotification = NotificationModel.fromJson(message.data);
          _notificationList ??= [];
          _notificationList!.insert(0, newNotification);
          notifyListeners();
        } catch (e) {
          print('Error parsing notification data: $e');
        }
      }

      // Check if there is a notification object (display notification)
      if (message.notification != null) {
        print('Message also contained a notification: ${message.notification}');
      }
    });
  }
}