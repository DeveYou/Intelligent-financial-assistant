import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/dio/dio_client.dart';
import 'package:intelligent_financial_assistant_frontend/data/datasource/remote/exception/api_error_handler.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/repositories/assistant_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class AssistantRepository implements AssistantRepositoryInterface{
  final DioClient dioClient;

  AssistantRepository({required this.dioClient});

  @override
  Future<ApiResponse> getMessages() async {
    final Response apiResponse = await dioClient.get(AppConstants.getMessagesUri);
    try {
      return ApiResponse.withSuccess(apiResponse);
    } catch (e) {
      return ApiResponse.withError('Failed to load assistant messages');
    }
  }

  @override
  Future<ApiResponse> sendMessage(String message) async {
    try {
      final response = await dioClient.post(
        AppConstants.assistantMessageUri,
        data: {'message': message},
      );
      return ApiResponse.withSuccess(response);
    } catch (e) {
      return ApiResponse.withError(ApiErrorHandler.getMessage(e));
    }
  }
}