import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/repositories/assistant_repository_interface.dart';
import 'package:intelligent_financial_assistant_frontend/features/assistant/domains/services/assistant_service_interface.dart';

class AssistantService implements AssistantServiceInterface{
  final AssistantRepositoryInterface assistantRepository;
  AssistantService({required this.assistantRepository});

  @override
  Future<ApiResponse> getMessages() async {
    return await assistantRepository.getMessages();
  }

  @override
  Future<ApiResponse> sendMessage(String message) async {
    return await assistantRepository.sendMessage(message);
  }
}