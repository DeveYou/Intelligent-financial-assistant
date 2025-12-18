
import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Repository interface for recipient data access.
///
/// Defines the contract for recipient repository implementations.
/// Repositories handle direct communication with the API client.
abstract class RecipientRepositoryInterface {
  /// Fetches the list of all recipients from the API.
  Future<ApiResponse> getRecipientList();
  
  /// Creates a new recipient via the API.
  ///
  /// [fullName] is the recipient's full name.
  /// [iban] is the recipient's International Bank Account Number.
  Future<ApiResponse> addRecipient(String fullName, String iban);
  
  /// Updates a recipient's information via the API.
  ///
  /// [id] is the unique identifier of the recipient.
  /// [fullName] is the updated full name.
  /// [iban] is the updated IBAN.
  Future<ApiResponse> updateRecipient(int id, String fullName, String iban);
  
  /// Deletes a recipient via the API.
  ///
  /// [id] is the unique identifier of the recipient to delete.
  Future<ApiResponse> deleteRecipient(int id);
}