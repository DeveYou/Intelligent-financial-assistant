import 'package:flutter/material.dart';
import '../../../../data/response/api_response.dart';
import '../domains/models/recipient_model.dart';
import '../domains/services/recipient_service_interface.dart';

enum RecipientState { loading, success, error }

class RecipientController with ChangeNotifier {
  final RecipientServiceInterface recipientService;

  RecipientController({required this.recipientService});

  List<RecipientModel>? _recipientList;
  RecipientState _recipientState = RecipientState.loading;
  String? _error;

  List<RecipientModel>? get recipientList => _recipientList;
  RecipientState get recipientState => _recipientState;
  bool get isLoading => _recipientState == RecipientState.loading;
  String? get error => _error;

  set _isLoading(bool isLoading) {}

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
        (apiResponse.response!.data as List).forEach((recipient) {
          _recipientList!.add(RecipientModel.fromJson(recipient));
        });
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