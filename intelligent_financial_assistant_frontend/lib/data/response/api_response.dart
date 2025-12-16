
import 'package:dio/dio.dart';

class ApiResponse {
  final Response? response;
  final dynamic error;

  ApiResponse(this.response, this.error);

  ApiResponse.withError(dynamic errorMessage) : response = null, error = errorMessage;

  ApiResponse.withSuccess(Response responseData) : response = responseData, error = null;
}