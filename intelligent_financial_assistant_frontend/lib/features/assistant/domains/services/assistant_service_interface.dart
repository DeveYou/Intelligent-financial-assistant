import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

abstract class AssistantServiceInterface {
  Future<ApiResponse> getMessages();
  Future<ApiResponse> sendMessage(String message);
}