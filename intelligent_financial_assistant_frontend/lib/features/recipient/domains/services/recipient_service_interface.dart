import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

abstract class RecipientServiceInterface {
  Future<ApiResponse> getRecipientList();
  Future<ApiResponse> addRecipient(String fullName, String iban);
  Future<ApiResponse> updateRecipient(int id, String fullName, String iban);
  Future<ApiResponse> deleteRecipient(int id);
}