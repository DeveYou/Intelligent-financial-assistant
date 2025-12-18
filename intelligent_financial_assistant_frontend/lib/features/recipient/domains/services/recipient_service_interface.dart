import 'package:intelligent_financial_assistant_frontend/data/response/api_response.dart';

/// Service interface for recipient-related operations.
///
/// Defines the contract for recipient service implementations.
/// All methods return [ApiResponse] for consistent error handling.
abstract class RecipientServiceInterface {
  /// Retrieves the list of all recipients for the current user.
  Future<ApiResponse> getRecipientList();
  
  /// Adds a new recipient to the user's recipients list.
  ///
  /// [fullName] is the recipient's full name.
  /// [iban] is the recipient's International Bank Account Number.
  Future<ApiResponse> addRecipient(String fullName, String iban);
  
  /// Updates an existing recipient's information.
  ///
  /// [id] is the unique identifier of the recipient to update.
  /// [fullName] is the new full name.
  /// [iban] is the new IBAN.
  Future<ApiResponse> updateRecipient(int id, String fullName, String iban);
  
  /// Deletes a recipient from the user's list.
  ///
  /// [id] is the unique identifier of the recipient to delete.
  Future<ApiResponse> deleteRecipient(int id);
}