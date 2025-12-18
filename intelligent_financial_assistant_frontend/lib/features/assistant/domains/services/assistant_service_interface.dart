import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Service interface for AI assistant operations.
///
/// Handles communication with the AI assistant API including
/// sending messages and retrieving chat history.
abstract class AssistantServiceInterface {
  /// Retrieves the chat message history from the API.
  Future<ApiResponse> getMessages();
  
  /// Sends a message to the AI assistant for processing.
  ///
  /// [message] is the user's text or voice input to process.
  /// Returns the AI's response with action intent and parameters.
  Future<ApiResponse> sendMessage(String message);
}