import 'package:date_format/date_format.dart';
import 'package:flutter/material.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/models/notification_model.dart';
import 'package:intelligent_financial_assistant_frontend/utils/dimensions.dart';

class NotificationCardWidget extends StatelessWidget {
  final NotificationModel notification;
  final VoidCallback? onTap;

  const NotificationCardWidget({super.key, required this.notification, this.onTap});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: EdgeInsets.all(Dimensions.paddingSizeSmall),
        color: notification.isRead == true
            ? Theme.of(context).cardColor
            : Theme.of(context).primaryColor.withOpacity(0.05),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  notification.title ?? '',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  formatDate(
                    notification.timestamp!,
                    [dd, '/', mm, '/', yyyy, ' ', HH, ':', nn],
                  ) ?? '',
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ],
            ),
            SizedBox(height: Dimensions.paddingSizeExtraSmall),
            Text(
              notification.message ?? '',
              style: Theme.of(context).textTheme.bodyMedium,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          ],
        ),
      ),
    );
  }
}