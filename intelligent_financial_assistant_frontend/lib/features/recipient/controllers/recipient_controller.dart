import 'package:flutter/material.dart';
import '../../../../data/response/api_response.dart';
import '../domains/models/recipient_model.dart';
import '../domains/services/recipient_service_interface.dart';

/// Represents the loading state of recipient operations.
enum RecipientState { 
  /// Data is being loaded from the API.
  loading, 
  
  /// Data has been successfully loaded.
  success, 
  
  /// An error occurred during the operation.
  error 
}

/// Manages recipient data and operations.
///
/// This controller handles CRUD operations for payment recipients,
/// including fetching, adding, updating, and deleting recipients.
/// It uses [ChangeNotifier] for state management.
class RecipientController with ChangeNotifier {
  /// The service interface for recipient operations.
  final RecipientServiceInterface recipientService;

  /// Creates a [RecipientController] with the required service.
  RecipientController({required this.recipientService});

  List<RecipientModel>? _recipientList;
  RecipientState _recipientState = RecipientState.loading;
  String? _error;

  /// Gets the list of recipients.
  List<RecipientModel>? get recipientList => _recipientList;
  
  /// Gets the current recipient state.
  RecipientState get recipientState => _recipientState;
  
  /// Returns true if data is currently being loaded.
  bool get isLoading => _recipientState == RecipientState.loading;
  
  /// Gets the error message if an error occurred.
  String? get error => _error;

  set _isLoading(bool isLoading) {}

  /// Retrieves the list of recipients from the API.
  ///
  /// If [reload] is true, forces a fresh fetch from the server.
  /// Otherwise, uses cached data if available.
  Future<void> getRecipientList({bool reload = false}) async {
    if (_recipientList == null || reload) {
      _recipientState = RecipientState.loading;
      _error = null;
      notifyListeners();
    }
    try {
      ApiResponse apiResponse = await recipientService.getRecipientList();
      if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
        _recipientList = [];
        var responseData = apiResponse.response!.data;
        if(responseData is Map && responseData.containsKey('data')) {
          responseData = responseData['data'];
        }
        if (responseData is List) {
          responseData.forEach((recipient) {
            _recipientList!.add(RecipientModel.fromJson(recipient));
          });
        }
        _recipientState = RecipientState.success;
      } else {
        _error = apiResponse.error.toString();
        _recipientState = RecipientState.error;
      }
    } catch (e) {
      _error = e.toString();
      _recipientState = RecipientState.error;
    } finally {
     notifyListeners();
    }
  }

  /// Adds a new recipient to the system.
  ///
  /// [fullName] is the recipient's full name.
  /// [iban] is the recipient's International Bank Account Number.
  ///
  /// Returns true if the operation was successful.
  Future<bool> addRecipient(String fullName, String iban) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await recipientService.addRecipient(fullName, iban);
    bool isSuccess = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      isSuccess = true;
      await getRecipientList(reload: true);
    } else {
      _error = apiResponse.error.toString();
    }
    _isLoading = false;
    notifyListeners();
    return isSuccess;
  }

  /// Updates an existing recipient's information.
  ///
  /// [id] is the unique identifier of the recipient to update.
  /// [fullName] is the new full name.
  /// [iban] is the new IBAN.
  ///
  /// Returns true if the operation was successful.
  Future<bool> updateRecipient(int id, String fullName, String iban) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await recipientService.updateRecipient(id, fullName, iban);
    bool isSuccess = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      isSuccess = true;
      await getRecipientList(reload: true);
    } else {
      _error = apiResponse.error.toString();
    }
    _isLoading = false;
    notifyListeners();
    return isSuccess;
  }

  /// Deletes a recipient from the system.
  ///
  /// [id] is the unique identifier of the recipient to delete.
  ///
  /// Returns true if the operation was successful.
  Future<bool> deleteRecipient(int id) async {
    _isLoading = true;
    notifyListeners();
    ApiResponse apiResponse = await recipientService.deleteRecipient(id);
    bool isSuccess = false;
    if (apiResponse.response != null && apiResponse.response!.statusCode == 200) {
      isSuccess = true;
      _recipientList?.removeWhere((element) => element.id == id);
      await getRecipientList(reload: true);
    } else {
      _error = apiResponse.error.toString();
    }
    _isLoading = false;
    notifyListeners();
    return isSuccess;
  }
}