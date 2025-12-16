
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

abstract class RecipientRepositoryInterface {
  Future<ApiResponse> getRecipientList();
  Future<ApiResponse> addRecipient(String fullName, String iban);
  Future<ApiResponse> updateRecipient(int id, String fullName, String iban); // Added
  Future<ApiResponse> deleteRecipient(int id); // Added
}