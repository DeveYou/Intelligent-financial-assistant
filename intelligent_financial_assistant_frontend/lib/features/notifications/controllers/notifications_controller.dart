import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/common/basewidgets/show_custom_snackbar_widget.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/models/notification_model.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/services/notifications_service_interface.dart';

import '../../../main.dart';

class NotificationsController with ChangeNotifier {
  final NotificationsServiceInterface notificationService;

  NotificationsController({required this.notificationService});

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  List<NotificationModel>? _notificationList;
  List<NotificationModel>? get notificationList => _notificationList;

  Future<void> getNotificationsList(bool reload) async {
    if (_notificationList == null || reload) {
      _isLoading = true;
      notifyListeners();
    }

    ApiResponse apiResponse = await notificationService.fetchNotifications();

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

  Future<void> markAsRead(int notificationId, int index) async {
    ApiResponse apiResponse = await notificationService.markAsRead(notificationId);
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      _notificationList![index].isRead = true; // Optimistic update
      notifyListeners();
    }
  }
}