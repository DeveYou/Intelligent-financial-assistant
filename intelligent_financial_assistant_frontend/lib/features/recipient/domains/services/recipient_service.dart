import '../../../../data/response/api_response.dart';
import '../repositories/recipient_repository_interface.dart';
import 'recipient_service_interface.dart';

class RecipientService implements RecipientServiceInterface {
  final RecipientRepositoryInterface recipientRepository;

  RecipientService({required this.recipientRepository});

  @override
  Future<ApiResponse> getRecipientList() async {
    return await recipientRepository.getRecipientList();
  }

  @override
  Future<ApiResponse> addRecipient(String fullName, String iban) async {
    return await recipientRepository.addRecipient(fullName, iban);
  }

  @override
  Future<ApiResponse> updateRecipient(int id, String fullName, String iban) async {
    return await recipientRepository.updateRecipient(id, fullName, iban);
  }

  @override
  Future<ApiResponse> deleteRecipient(int id) async {
    return await recipientRepository.deleteRecipient(id);
  }
}