import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/controllers/notifications_controller.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/widgets/notification_card_widget.dart';
import 'package:intelligent_financial_assistant_frontend/localization/language_constraints.dart';
import 'package:intelligent_financial_assistant_frontend/utils/dimensions.dart';
import 'package:provider/provider.dart';// Assuming generic app bar exists

/// Notifications screen displaying user notifications.
///
/// Shows a list of notifications with pull-to-refresh functionality.
/// Allows marking notifications as read by tapping on them.
class NotificationsScreen extends StatefulWidget {
  /// Creates a [NotificationsScreen].
  const NotificationsScreen({super.key});

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen> {
  @override
  void initState() {
    super.initState();
    // Fetch data on initialization
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<NotificationsController>(context, listen: false).getNotificationsList(true);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(getTranslated("notifications", context)!, style: Theme.of(context).textTheme.titleLarge),
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: IconThemeData(color: Theme.of(context).textTheme.bodyLarge?.color),
      ),
      body: Consumer<NotificationsController>(
        builder: (context, notificationController, child) {
          return notificationController.isLoading
              ? const Center(child: CircularProgressIndicator())
              : RefreshIndicator(
            onRefresh: () async {
              await notificationController.getNotificationsList(true);
            },
            child: notificationController.notificationList != null &&
                notificationController.notificationList!.isNotEmpty
                ? ListView.builder(
              itemCount: notificationController.notificationList!.length,
              padding: EdgeInsets.all(Dimensions.paddingSizeSmall),
              itemBuilder: (context, index) {
                return NotificationCardWidget(
                  notification: notificationController.notificationList![index],
                  onTap: () {
                    notificationController.markAsRead(
                        notificationController.notificationList![index].id!,
                        index
                    );
                  },
                );
              },
            )
                : const Center(child: Text("No notifications available")),
          );
        },
      ),
    );
  }
}