import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Interface defining the contract for the AI assistant feature.
abstract class AssistantRepositoryInterface {
  /// Retrieves the chat history/messages.
  Future<ApiResponse> getMessages();

  /// Sends a new [message] to the AI assistant.
  Future<ApiResponse> sendMessage(String message);
}