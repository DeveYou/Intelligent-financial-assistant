import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/notifications/domains/repositories/notifications_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class NotificationsRepository implements NotificationsRepositoryInterface {
  final DioClient dioClient;

  NotificationsRepository({required this.dioClient});

  @override
  Future<ApiResponse> fetchNotifications() async {
    try {
      // Replace with your actual endpoint URI defined in AppConstants
      Response response = await dioClient.get(AppConstants.getNotificationsUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(e);
    }
  }

  @override
  Future<ApiResponse> markAsRead(int notificationId) async {
    try {
      Response response = await dioClient.post(
          AppConstants.markNotificationAsReadUri,
          data: {'id': notificationId}
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(e);
    }
  }

  @override
  Future<ApiResponse> sendNotification(Map<String, dynamic> data) async {
    try {
      Response response = await dioClient.post(
          AppConstants.sendNotificationUri,
          data: data
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(e);
    }
  }
}