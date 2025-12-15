import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

import 'recipient_repository_interface.dart';

class RecipientRepository implements RecipientRepositoryInterface {
  final DioClient dioClient;

  RecipientRepository({required this.dioClient});

  @override
  Future<ApiResponse> getRecipientList() async {
    try {
      final response = await dioClient.get(AppConstants.getRecipientsUri);
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> addRecipient(String fullName, String iban) async {
    try {
      final response = await dioClient.post(
        AppConstants.addRecipientUri,
        data: {'full_name': fullName, 'iban': iban},
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> updateRecipient(int id, String fullName, String iban) async {
    try {
      final response = await dioClient.put(
        '${AppConstants.updateRecipientUri}$id',
        data: {'full_name': fullName, 'iban': iban},
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }

  @override
  Future<ApiResponse> deleteRecipient(int id) async {
    try {
      final response = await dioClient.delete(
        '${AppConstants.deleteRecipientUri}$id',
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }
}