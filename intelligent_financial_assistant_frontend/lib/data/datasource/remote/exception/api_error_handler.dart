import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:intelligent_financial_assistant_frontend/data/response/error_response.dart';
import 'package:intelligent_financial_assistant_frontend/features/authentication/controllers/authentication_controller.dart';
import 'package:intelligent_financial_assistant_frontend/main.dart';
import 'package:provider/provider.dart';

class ApiErrorHandler {
  static dynamic getMessage(error) {
    dynamic errorDescription = "";
    if (error is Exception) {
      try {
        if (error is DioException) {
          log("DioException Type: ${error.type}");
          log("Response Data: ${error.response?.data}");
          log("Response Status Code: ${error.response?.statusCode}");

          switch (error.type) {
            case DioExceptionType.cancel:
              errorDescription = "Request to API server was cancelled";
              break;
            case DioExceptionType.connectionTimeout:
              errorDescription = "Connection timeout with API server";
              break;
            case DioExceptionType.sendTimeout:
              errorDescription = "Send timeout";
              break;
            case DioExceptionType.receiveTimeout:
              errorDescription = "Receive timeout in connection with API server";
              break;
            case DioExceptionType.badResponse:
              switch (error.response!.statusCode) {
                case 403:
                  log("403 Error Response: ${error.response!.data}");
                  if(error.response!.data['errors'] != null){
                    ErrorResponse errorResponse = ErrorResponse.fromJson(error.response?.data);
                    errorDescription = errorResponse.errors?[0].message;
                    log("403 Error Message: $errorDescription");
                  }else{
                    errorDescription = error.response!.data['message'];
                    log("403 Message: $errorDescription");
                  }
                  break;
                case 401:
                  log("401 Error Response: ${error.response!.data}");
                  if(error.response!.data['errors'] != null){
                    ErrorResponse errorResponse = ErrorResponse.fromJson(error.response?.data);
                    errorDescription = errorResponse.errors?[0].message;
                    log("401 Error Message: $errorDescription");
                  }else{
                    errorDescription = error.response!.data['message'];
                    log("401 Message: $errorDescription");
                  }
                  Provider.of<AuthenticationController>(Get.context!,listen: false).clearSharedData();
                  break;
                case 404:
                case 403:
                  log("404/403 Error Response: ${error.response!.data}");
                case 500:
                  log("500 Error Response: ${error.response!.data}");
                case 503:
                case 429:
                  errorDescription = error.response!.statusMessage;
                  log("Status Message: $errorDescription");
                  break;
                default:
                  log("Default Error Response: ${error.response!.data}");
                  ErrorResponse errorResponse = ErrorResponse.fromJson(error.response!.data);
                  if (errorResponse.errors != null && errorResponse.errors!.isNotEmpty) {
                    errorDescription = errorResponse;
                    log("Error Response Object: $errorDescription");
                  } else {
                    errorDescription = "Failed to load data - status code: ${error.response!.statusCode}";
                    log("Default Error Message: $errorDescription");
                  }
              }
              break;
            case DioExceptionType.badCertificate:
              log("Bad Certificate Error");
              break;
            case DioExceptionType.connectionError:
              log("Connection Error");
              break;
            case DioExceptionType.unknown:
              errorDescription = "Request to API call limit excited ";
              log("Unknown Error: $errorDescription");
              break;
          }
        } else {
          errorDescription = "Unexpected error occurred";
          log("Non-Dio Exception: $errorDescription");
        }
      } on FormatException catch (e) {
        errorDescription = e.toString();
        log("Format Exception: $errorDescription");
      }
    } else {
      errorDescription = "is not a subtype of exception";
      log("Non-Exception Error: $errorDescription");
    }
    return errorDescription;
  }
}