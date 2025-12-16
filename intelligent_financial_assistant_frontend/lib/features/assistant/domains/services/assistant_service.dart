import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:google_generative_ai/google_generative_ai.dart' hide RequestOptions;
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/repositories/assistant_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/services/assistant_service_interface.dart';
import 'package:intelligent_financial_assistant_frontend/utils/app_constants.dart';

class AssistantService implements AssistantServiceInterface{
  final AssistantRepositoryInterface assistantRepository;
  AssistantService({required this.assistantRepository});

  @override
  Future<ApiResponse> getMessages() async {
    return await assistantRepository.getMessages();
  }

  @override
  Future<ApiResponse> sendMessage(String message) async {
      try {
        final model = GenerativeModel(
          model: AppConstants.geminiModel,
          apiKey: AppConstants.geminiApiToken,
          generationConfig: GenerationConfig(responseMimeType: 'application/json'),
          systemInstruction: Content.system(
              'You are a banking assistant. Return strictly a JSON object based on the user intent. Do not return markdown or conversational text.\n'
              'Input: "How much money do I have?" / "Give me my balance" -> Output: {"action": "getBalance"}\n'
              'Input: "Check my last transaction" -> Output: {"action": "getLastTransaction"}\n'
              'Input: "Send 500 dollars to Sarah" / "Transfer money to John" -> Output: { "action": "sendMoney", "amount": 500.0, "recipient": "name" }'
          ),
        );

        final content = [Content.text(message)];
        final response = await model.generateContent(content);

        if (response.text != null) {
          try {
            String rawText = response.text!.replaceAll(RegExp(r'```json|```'), '').trim();
            final jsonResponse = jsonDecode(rawText);
            return ApiResponse.withSuccess(Response(
                requestOptions: RequestOptions(path: AppConstants.assistantMessageUri),
                data: jsonResponse,
                statusCode: 200));
          } catch (e) {
            return ApiResponse.withError("Failed to parse AI response: $e");
          }
        } else {
          return ApiResponse.withError("No response from AI");
        }
      } catch (e) {
        return ApiResponse.withError(e.toString());
      }
  }
}